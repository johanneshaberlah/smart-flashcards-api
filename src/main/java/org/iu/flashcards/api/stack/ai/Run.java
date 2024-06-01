package org.iu.flashcards.api.stack.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Run(String id, String status) {
}
