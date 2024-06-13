package org.iu.flashcards.api.stack.assistant.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.iu.flashcards.api.card.Card;
import org.iu.flashcards.api.card.CardContext;
import org.iu.flashcards.api.card.CardService;
import org.iu.flashcards.api.stack.assistant.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;

import java.io.IOException;

@Component
@Scope("singleton")
public class AssistantCardFactory {
  private final ObjectMapper objectMapper;
  private final CardService cardService;

  @Autowired
  public AssistantCardFactory(ObjectMapper objectMapper, CardService cardService) {
    this.objectMapper = objectMapper;
    this.cardService = cardService;
  }

  public boolean processCards(String stackId, Message message, RequestAttributes attributes) {
    RequestContextHolder.setRequestAttributes(attributes);
    System.out.println("Processing cards...");
    if (message.content().isEmpty()) {
      return false;
    }
    var response = message.content().get(0).text()
      .value()
      .replace("`", "")
      .replace("json", "")
      .replace("\n", "");

    CardResponse[] cards;
    try {
      cards = objectMapper.readValue(response, CardResponse[].class);
    } catch (IOException failure) {
      return false;
    }
    for (CardResponse card : cards) {
      cardService.createCard(new CardContext(stackId, null, card.question(), card.answer()));
    }
    return true;
  }

  public CardService cardService() {
    return cardService;
  }
}
