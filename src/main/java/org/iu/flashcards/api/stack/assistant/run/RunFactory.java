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
import java.util.Map;
import java.util.Optional;
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

  public void createRunWithStreaming(Thread thread, Consumer<Message> messageConsumer) {
    Map<String, Object> payload = Map.of(
      "assistant_id", credentials.assistantId(),
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
    OkHttpClient client = new OkHttpClient();
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
              if (line.contains("event: thread.message.completed")) {
                System.out.println("RECEIVED MESSAGE COMPLETED EVENT");
                var data = response.body().source().readUtf8Line();
                System.out.println("Payload: " + data);
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
