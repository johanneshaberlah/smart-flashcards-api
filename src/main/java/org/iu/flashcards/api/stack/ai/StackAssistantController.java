package org.iu.flashcards.api.stack.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class StackAssistantController {
  private final StackAssistantService stackAssistantService;

  @Autowired
  public StackAssistantController(StackAssistantService stackAssistantService) {
    this.stackAssistantService = stackAssistantService;
  }

  @PostMapping("/stack/{stackId}/createFromFile")
  public ResponseEntity<?> createFromFile(@PathVariable("stackId") String stackId, @RequestParam("file") MultipartFile file, @RequestParam("custom-instructions") String customInstructions) {
    return stackAssistantService.createFromFile(stackId, file, customInstructions);
  }
}
