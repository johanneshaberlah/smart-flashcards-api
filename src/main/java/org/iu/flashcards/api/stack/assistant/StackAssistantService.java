package org.iu.flashcards.api.stack.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.iu.flashcards.api.card.CardContext;
import org.iu.flashcards.api.card.CardService;
import org.iu.flashcards.api.stack.StackNotFoundException;
import org.iu.flashcards.api.stack.StackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class StackAssistantService {
  private static final int TIMEOUT_SECONDS = 180                                                                     ;
  private static final int CHECK_INTERVAL_MS = 1000;
  private final Logger logger = LoggerFactory.getLogger(StackAssistantService.class);

  private final String openAiKey;
  private final StackService stackService;
  private final CardService cardService;
  private final FileUploadService fileUploadService;
  private final ObjectMapper objectMapper;

  @Autowired
  public StackAssistantService(@Value("${open-ai-key}") String openAiKey, StackService stackService, CardService cardService, FileUploadService fileUploadService, ObjectMapper objectMapper) {
    this.openAiKey = openAiKey;
    this.stackService = stackService;
    this.cardService = cardService;
    this.fileUploadService = fileUploadService;
    this.objectMapper = objectMapper;
  }

  public ResponseEntity<?> createFromFile(String stackId, MultipartFile multipartFile, String customInstructions) {
    try {
      logger.info("Received request to create cards from file");
      stackService.findStack(stackId);
      logger.info("Stack found (" + stackId + ")");
      File file = fileUploadService.upload(multipartFile);
      if (!file.status().equals("processed")) {
        return ResponseEntity.internalServerError().body("File processing failed");
      }
      logger.info("File uploaded " + file.id());
      Thread thread = createThread();
      logger.info("Thread created " + thread.id());
      var prompt = "Hier ist das Folienskript einer Vorlesung in der Datei 'Skript.pdf' (" + file.id() + "). Analysiere das Dokument und erstelle Karteikarten im besprochenen Format." + (!customInstructions.isEmpty() ? "Außerdem gibt es folgenden Anmerkungen zum Inhalt der Karteikarte: " + customInstructions + "." : "");
      Message message = createMessage(thread, file, prompt);
      logger.info("Prompt: " + prompt);

      Run run = createRun(thread);
      logger.info("Run created " + run.id());

      AtomicBoolean completedNormally = new AtomicBoolean(false);

      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
      ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
        try {
          Run currentRunStatus = retrieveRun(thread, run);
          if (currentRunStatus.status().equalsIgnoreCase("completed")) {
            logger.info("Card generation finished");
            completedNormally.set(true);
            scheduler.shutdown();
          }
        } catch (Exception e) {
          e.printStackTrace();
          scheduler.shutdown();
        }
      }, 0, CHECK_INTERVAL_MS, TimeUnit.MILLISECONDS);

      scheduler.schedule(() -> {
        if (!scheduler.isShutdown()) {
          scheduledFuture.cancel(true);
          scheduler.shutdown();
        }
      }, TIMEOUT_SECONDS, TimeUnit.SECONDS);

      scheduler.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
      if (completedNormally.get()) {
        if (readMessages(thread).data().isEmpty()) {
          return ResponseEntity.internalServerError().body("No response from OpenAI.");
        }
        if (readMessages(thread).data().get(0).content().isEmpty()) {
          return ResponseEntity.internalServerError().body("No response from OpenAI.");
        }
        var response = readMessages(thread).data().get(0).content().get(0).text();
        logger.info("Response from OpenAI: " + response.value());
        var cards = objectMapper.readValue(response.value(), CardResponse[].class);
        for (CardResponse card : cards) {
          cardService.createCard(new CardContext(stackId, null, card.question(), card.answer()));
        }
      } else {
        logger.error("Timeout reached while requesting OpenAI.");
        return ResponseEntity.internalServerError().body("Timeout reached while requesting OpenAI.");
      }
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().body(e.getMessage());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (StackNotFoundException stackNotFoundException) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().build();
  }

  private Thread createThread() throws IOException {
    OkHttpClient client = new OkHttpClient();
    // Erstellen der Anfrage
    RequestBody payload = RequestBody.create("", MediaType.parse("application/json"));

    Request request = new Request.Builder()
      .url("https://api.openai.com/v1/threads")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + openAiKey)
      .header("OpenAI-Beta", "assistants=v2")
      .post(payload)
      .build();
    // Senden der Anfrage und Abrufen der Antwort
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      // Ausgabe der Antwort
      return objectMapper.readValue(response.body().string(), Thread.class);
    }
  }

  private Message createMessage(Thread thread, File file, String message) throws IOException {
    OkHttpClient client = new OkHttpClient();
    // Erstellen der Anfrage
    // Erstellen der Payload-Map
    Map<String, Object> payload = Map.of(
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
    String json = objectMapper.writeValueAsString(payload);
    RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
    Request request = new Request.Builder()
      .url("https://api.openai.com/v1/threads/" + thread.id() + "/messages")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + openAiKey)
      .header("OpenAI-Beta", "assistants=v2")
      .post(body)
      .build();
    // Senden der Anfrage und Abrufen der Antwort
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      // Ausgabe der Antwort
      return objectMapper.readValue(response.body().string(), Message.class);
    }
  }

  private Run createRun(Thread thread) throws IOException {
    OkHttpClient client = new OkHttpClient();

    // Erstellen der Payload-Map
    Map<String, String> payload = Map.of(
      "assistant_id", "asst_fFvNIq18W2pBX5XGAozGXUpc"
    );

    // Konvertieren der Payload in JSON
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonPayload = objectMapper.writeValueAsString(payload);

    // Erstellen des Request-Bodys
    RequestBody requestBody = RequestBody.create(jsonPayload, MediaType.parse("application/json"));

    // Erstellen der Anfrage
    Request request = new Request.Builder()
      .url("https://api.openai.com/v1/threads/" + thread.id() + "/runs")
      .header("Authorization", "Bearer " + openAiKey)
      .header("Content-Type", "application/json")
      .header("OpenAI-Beta", "assistants=v2")
      .post(requestBody)
      .build();

    // Senden der Anfrage und Abrufen der Antwort
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      return objectMapper.readValue(response.body().string(), Run.class);
    }
  }

  private Run retrieveRun(Thread thread, Run run) throws IOException {
    OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder()
      .url("https://api.openai.com/v1/threads/" + thread.id() + "/runs/" + run.id())
      .header("Authorization", "Bearer " + openAiKey)
      .header("Content-Type", "application/json")
      .header("OpenAI-Beta", "assistants=v2")
      .get()
      .build();

    // Senden der Anfrage und Abrufen der Antwort
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      return objectMapper.readValue(response.body().string(), Run.class);
    }
  }

  private MessageResponse readMessages(Thread thread) throws IOException {
    OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder()
      .url("https://api.openai.com/v1/threads/" + thread.id() + "/messages")
      .header("Authorization", "Bearer " + openAiKey)
      .header("Content-Type", "application/json")
      .header("OpenAI-Beta", "assistants=v2")
      .get()
      .build();

    // Senden der Anfrage und Abrufen der Antwort
    try (Response response = client.newCall(request).execute()) {
      var body = response.body().string();
      logger.info("OpenAI Response:");
      logger.info(body);
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      return objectMapper.readValue(body, MessageResponse.class);
    }
  }
}
