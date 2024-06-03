package org.iu.flashcards.api.stack;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.iu.flashcards.api.common.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Stack", description = "Operations related to Flashcard stacks")
public class StackController {
  private final StackService stackService;

  @Autowired
  public StackController(StackService stackService) {
    this.stackService = stackService;
  }

  @Operation(summary = "Create a new stack")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Stack created",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Stack.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @PostMapping("/stack")
  public ResponseEntity<Stack> createStack(@RequestBody StackContext stackContext) {
    return ResponseEntity.ok(stackService.createStack(stackContext));
  }

  @Operation(summary = "List all stacks")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Stacks retrieved",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Stack.class)))
  })
  @GetMapping("/stack")
  public ResponseEntity<List<Stack>> listStacks() {
    return ResponseEntity.ok(stackService.stacks());
  }

  @Operation(summary = "Retrieve a stack by its ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Stack found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Stack.class))),
    @ApiResponse(responseCode = "404", description = "Stack not found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @GetMapping("/stack/{stack}")
  public ResponseEntity<?> readStack(@PathVariable("stack") String uniqueId) {
    try {
      return ResponseEntity.ok(stackService.findStack(uniqueId));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    }
  }

  @Operation(summary = "Delete a stack by its ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Stack deleted",
      content = @Content),
    @ApiResponse(responseCode = "404", description = "Stack not found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @DeleteMapping("/stack/{stack}")
  public ResponseEntity<?> deleteStack(@PathVariable("stack") String uniqueId) {
    try {
      return ResponseEntity.ok(stackService.deleteStack(uniqueId));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    }
  }
}
