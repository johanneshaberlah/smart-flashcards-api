package org.iu.flashcards.api.stack.assistant;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Configuration
public class AssistantConfiguration {

  @Bean
  @Scope("singleton")
  ObjectMapper objectMapper() {
    return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  }

  @Bean
  @Scope("singleton")
  public OkHttpClient httpClient(AssistantCredentials credentials) {
    return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
      @NotNull
      @Override
      public Response intercept(@NotNull Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
          .addHeader("Content-Type", "application/json")
          .addHeader("OpenAI-Beta", "assistants=v2")
          .addHeader("Authorization", "Bearer " + credentials.apiKey())
          .build());
      }
    }).build();
  }
}
