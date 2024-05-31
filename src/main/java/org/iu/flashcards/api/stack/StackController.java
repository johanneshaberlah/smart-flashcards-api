package org.iu.flashcards.api.stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StackController {
  private final StackService stackService;

  @Autowired
  public StackController(StackService stackService) {
    this.stackService = stackService;
  }

  @PostMapping("/stack")
  public ResponseEntity<Stack> createStack(@RequestBody StackContext stackContext) {
    return ResponseEntity.ok(stackService.createStack(stackContext));
  }

  @GetMapping("/stack")
  public ResponseEntity<List<Stack>> listStacks() {
    return ResponseEntity.ok(stackService.stacks());
  }

  @GetMapping("/stack/{stack}")
  public ResponseEntity<?> readStack(@PathVariable("stack") String uniqueId) {
    try {
      return ResponseEntity.ok(stackService.findStack(uniqueId));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    }
  }

  @DeleteMapping("/stack/{stack}")
  public ResponseEntity<?> deleteStack(@PathVariable("stack") String uniqueId) {
    try {
      return ResponseEntity.ok(stackService.deleteStack(uniqueId));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    }
  }
}
