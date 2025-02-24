package org.iu.flashcards.api.stack.assistant.run;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.iu.flashcards.api.stack.assistant.AssistantCredentials;
import org.iu.flashcards.api.stack.assistant.message.Message;
import org.iu.flashcards.api.stack.assistant.thread.Thread;
import org.iu.flashcards.api.stack.assistant.thread.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Component
@Scope("singleton")
public class RunFactory {
  private final Logger log = LoggerFactory.getLogger(RunFactory.class);

  private final OkHttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final AssistantCredentials credentials;

  @Autowired
  public RunFactory(OkHttpClient httpClient, ObjectMapper objectMapper, AssistantCredentials credentials) {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
    this.credentials = credentials;
  }

  public void createRunWithStreaming(Thread thread, String assistant, Consumer<Message> messageConsumer) {
    Map<String, Object> payload = Map.of(
      "assistant_id", assistant,
      "stream", true
    );
    String jsonPayload;
    try {
      jsonPayload = objectMapper.writeValueAsString(payload);
    } catch (IOException failure) {
      log.error("", failure);
      return;
    }
    RequestBody requestBody = RequestBody.create(jsonPayload, MediaType.parse("application/json"));

    Request request = new Request.Builder()
      .url("https://api.openai.com/v1/threads/" + thread.id() + "/runs")
      .addHeader("Content-Type", "application/json")
      .addHeader("OpenAI-Beta", "assistants=v2")
      .addHeader("Authorization", "Bearer " + credentials.apiKey())
      .post(requestBody)
      .build();

    executeWithStreaming(request, messageConsumer);
  }

  private void executeWithStreaming(Request request, Consumer<Message> consumer) {
    System.out.println("Launching client with increased timeout");
    OkHttpClient client = new OkHttpClient.Builder()
      .callTimeout(Duration.of(5, ChronoUnit.MINUTES))
      .readTimeout(Duration.of(5, ChronoUnit.MINUTES))
      .connectTimeout(Duration.of(5, ChronoUnit.MINUTES))
      .writeTimeout(Duration.of(5, ChronoUnit.MINUTES))
      .build();
    AtomicBoolean shouldContinue = new AtomicBoolean(true);
    var call = client.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        shouldContinue.set(false);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
          System.out.println(response);
          System.out.println("Server responded with error");
          shouldContinue.set(false);
          return;
        }
        try {
          if (response.body() != null) {
            String line;
            while (shouldContinue.get() && (line = response.body().source().readUtf8Line()) != null) {
              System.out.println(line);
              if (line.contains("event: thread.message.completed")) {
                var data = response.body().source().readUtf8Line();
                consumer.accept(objectMapper.readValue(data.replace("data: ", ""), Message.class));
                shouldContinue.set(false);  // Set false to stop further processing
              }
            }
          }
        } catch (IOException failure) {
          failure.printStackTrace();
          consumer.accept(null);
        } finally {
          System.out.println("Closing response");
          response.close();
          shouldContinue.set(false);
        }
      }
    });

    // Replace System.in.read() with a loop that checks the flag.
    while (shouldContinue.get()) {
      try {
        java.lang.Thread.sleep(100); // Sleep briefly to avoid busy waiting
      } catch (InterruptedException e) {
        java.lang.Thread.currentThread().interrupt();
        break;
      }
    }
  }
}
