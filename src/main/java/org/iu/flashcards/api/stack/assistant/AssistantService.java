package org.iu.flashcards.api.stack.assistant;

import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import org.iu.flashcards.api.card.Card;
import org.iu.flashcards.api.stack.StackService;
import org.iu.flashcards.api.stack.assistant.card.AssistantCardFactory;
import org.iu.flashcards.api.stack.assistant.file.FileUploadService;
import org.iu.flashcards.api.stack.assistant.message.MessageFactory;
import org.iu.flashcards.api.stack.assistant.run.RunFactory;
import org.iu.flashcards.api.stack.assistant.thread.Thread;
import org.iu.flashcards.api.stack.assistant.thread.ThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AssistantService {
  private static final String PROMPT = "Hier ist das Folienskript einer Vorlesung in der Datei 'Skript.pdf' (%s). Analysiere das Dokument und erstelle Karteikarten im besprochenen Format.";

  private final AssistantCredentials credentials;
  private final StackService stackService;
  private final ThreadFactory threadFactory;
  private final MessageFactory messageFactory;
  private final RunFactory runFactory;
  private final AssistantCardFactory cardFactory;
  private final FileUploadService fileUploadService;

  @Autowired
  public AssistantService(
    AssistantCredentials credentials,
    StackService stackService,
    ThreadFactory threadFactory,
    MessageFactory messageFactory,
    RunFactory runFactory,
    FileUploadService fileUploadService,
    AssistantCardFactory cardFactory
  ) {
    this.credentials = credentials;
    this.stackService = stackService;
    this.threadFactory = threadFactory;
    this.messageFactory = messageFactory;
    this.runFactory = runFactory;
    this.fileUploadService = fileUploadService;
    this.cardFactory = cardFactory;
    cardFactory.cardService().registerCardCreationListener(this::createHint);
  }


  public ResponseEntity<?> createFromFile(String stackId, MultipartFile multipartFile, String customInstructions) throws InterruptedException {
    Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
    long start = System.currentTimeMillis();
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    stackService.findStack(stackId);
    var file = fileUploadService.upload(multipartFile).orElseThrow();
    Thread thread = threadFactory.createThread().orElseThrow();
    var prompt = String.format(PROMPT, file.id()) + (!customInstructions.isEmpty() ? "Außerdem gibt es folgenden Anmerkungen zum Inhalt der Karteikarte: " + customInstructions + "." : "");
    messageFactory.writeMessage(thread, file, prompt);
    runFactory.createRunWithStreaming(thread, credentials.assistantId(), message -> {
      System.out.println("Waiting for thread...");
      cardFactory.processCards(stackId, message, attributes);
    });
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
    return ResponseEntity.ok().build();
  }

  public void createHint(Card card) {
    Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
    System.out.println("Creating hint for card " + card.toString());
    Thread thread = threadFactory.createThread().orElseThrow();
    var prompt = String.format("Frage: %s \n Antwort: %s", card.getQuestion(), card.getAnswer());
    messageFactory.writeMessage(thread, prompt);
    runFactory.createRunWithStreaming(thread, credentials.hintMachine(), message -> {
      var hint = message.content().get(0).text().value();
      card.setHint(hint);
      System.out.println("Hint: " + hint);
      cardFactory.cardService().save(card);
    });
  }

  @Scheduled(initialDelay = 0, fixedRate = 2, timeUnit = TimeUnit.MINUTES)
  public void createHintForOldCards() {
    cardFactory.cardService().findAll()
      .stream()
      .limit(100)
      .filter(card -> card.getHint() == null || card.getHint().isEmpty())
      .forEach(this::createHint);
  }
}
