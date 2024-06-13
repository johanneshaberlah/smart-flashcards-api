package org.iu.flashcards.api.stack.assistant.run;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Run(String id, String status) {
}
