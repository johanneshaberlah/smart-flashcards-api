package org.iu.flashcards.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "Smart-Flashcards API", description = "With this API you can manage your flashcards.", version = "0.1.0"))
public class SmartFlashcardsApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SmartFlashcardsApiApplication.class, args);
  }
}
