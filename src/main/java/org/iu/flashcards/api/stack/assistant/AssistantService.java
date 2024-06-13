package org.iu.flashcards.api.stack.assistant;

import org.iu.flashcards.api.stack.StackService;
import org.iu.flashcards.api.stack.assistant.card.AssistantCardFactory;
import org.iu.flashcards.api.stack.assistant.file.FileUploadService;
import org.iu.flashcards.api.stack.assistant.message.MessageFactory;
import org.iu.flashcards.api.stack.assistant.run.RunFactory;
import org.iu.flashcards.api.stack.assistant.thread.Thread;
import org.iu.flashcards.api.stack.assistant.thread.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AssistantService {
  private static final int TIMEOUT_SECONDS = 180;
  private static final String PROMPT = "Hier ist das Folienskript einer Vorlesung in der Datei 'Skript.pdf' (%s). Analysiere das Dokument und erstelle Karteikarten im besprochenen Format.";

  private static final int CHECK_INTERVAL_MS = 500;
  private final Logger logger = LoggerFactory.getLogger(AssistantService.class);

  private final StackService stackService;
  private final ThreadFactory threadFactory;
  private final MessageFactory messageFactory;
  private final RunFactory runFactory;
  private final AssistantCardFactory cardFactory;
  private final FileUploadService fileUploadService;

  @Autowired
  public AssistantService(
    StackService stackService,
    ThreadFactory threadFactory,
    MessageFactory messageFactory,
    RunFactory runFactory,
    FileUploadService fileUploadService,
    AssistantCardFactory cardFactory
  ) {
    this.stackService = stackService;
    this.threadFactory = threadFactory;
    this.messageFactory = messageFactory;
    this.runFactory = runFactory;
    this.fileUploadService = fileUploadService;
    this.cardFactory = cardFactory;
  }

  public ResponseEntity<?> createFromFile(String stackId, MultipartFile multipartFile, String customInstructions) throws InterruptedException {
    long start = System.currentTimeMillis();
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    stackService.findStack(stackId);
    var file = fileUploadService.upload(multipartFile).orElseThrow();
    Thread thread = threadFactory.createThread().orElseThrow();
    var prompt = String.format(PROMPT, file.id()) + (!customInstructions.isEmpty() ? "Außerdem gibt es folgenden Anmerkungen zum Inhalt der Karteikarte: " + customInstructions + "." : "");
    messageFactory.writeMessage(thread, file, prompt);
    runFactory.createRunWithStreaming(thread, message -> {
      System.out.println("Waiting for thread...");
      cardFactory.processCards(stackId, message, attributes);
    });
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
    return ResponseEntity.ok().build();
  }
}
