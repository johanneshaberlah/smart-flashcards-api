package org.iu.flashcards.api.stack.assistant.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.iu.flashcards.api.stack.assistant.file.File;
import org.iu.flashcards.api.stack.assistant.thread.Thread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Scope("singleton")
public class MessageFactory {
  private final Logger log = LoggerFactory.getLogger(MessageFactory.class);

  private final OkHttpClient httpClient;
  private final ObjectMapper objectMapper;

  @Autowired
  public MessageFactory(OkHttpClient httpClient, ObjectMapper objectMapper) {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  public void writeMessage(Thread thread, File file, String message) {
    Map<String, Object> payload = createPayload(file, message);
    String json;
    try {
      json = objectMapper.writeValueAsString(payload);
    } catch (IOException failure) {
      log.error("Cannot write payload to json", failure);
      return;
    }
    RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
    Request request = new Request.Builder()
      .url("https://api.openai.com/v1/threads/" + thread.id() + "/messages")
      .post(body)
      .build();
    try {
      httpClient.newCall(request).execute();
    } catch (IOException failure) {
      log.error("Creating a thread caused an exception", failure);
    }
  }

  private Map<String, Object> createPayload(File file, String message) {
    return Map.of(
      "role", "user",
      "content", message,
      "attachments", List.of(
        Map.of(
          "file_id", file.id(),
          "tools", List.of(
            Map.of("type", "file_search")
          )
        )
      )
    );
  }
}
