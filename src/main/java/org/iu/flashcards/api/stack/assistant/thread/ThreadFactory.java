package org.iu.flashcards.api.stack.assistant.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Scope("singleton")
public class ThreadFactory {
  private final Logger log = LoggerFactory.getLogger(ThreadFactory.class);

  private final OkHttpClient httpClient;
  private final ObjectMapper objectMapper;

  @Autowired
  public ThreadFactory(OkHttpClient httpClient, ObjectMapper objectMapper) {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  public Optional<Thread> createThread() {
    RequestBody payload = RequestBody.create("", MediaType.parse("application/json"));
    Request request = new Request.Builder()
      .url("https://api.openai.com/v1/threads")
      .post(payload)
      .build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (response.body() == null) {
        return Optional.empty();
      }
      return Optional.ofNullable(objectMapper.readValue(response.body().string(), Thread.class));
    } catch (IOException failure) {
      log.error("Creating a thread caused an exception", failure);
      return Optional.empty();
    }
  }
}
