package org.iu.flashcards.api.stack.assistant.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.iu.flashcards.api.stack.assistant.thread.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class FileUploadService {
  private final Logger log = LoggerFactory.getLogger(FileUploadService.class);
  private static final String API_URL = "https://api.openai.com/v1/files";

  private final ObjectMapper objectMapper;
  private final OkHttpClient httpClient;

  @Autowired
  public FileUploadService(ObjectMapper objectMapper, OkHttpClient httpClient) {
    this.objectMapper = objectMapper;
    this.httpClient = httpClient;
  }

  public Optional<File> upload(MultipartFile multipartFile) {
    RequestBody fileBody;
    try {
      fileBody = RequestBody.create(
        MediaType.parse(multipartFile.getContentType()),
        multipartFile.getBytes()
      );
    } catch (IOException failure) {
      failure.printStackTrace();
      return Optional.empty();
    }

    // Build the multipart request body
    MultipartBody requestBody = new MultipartBody.Builder()
      .setType(MultipartBody.FORM)
      .addFormDataPart("file", "Skript.pdf", fileBody)
      .addFormDataPart("purpose", "assistants")
      .build();

    // Build the request
    Request request = new Request.Builder()
      .url(API_URL)
      .post(requestBody)
      .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (response.body() == null) {
        return Optional.empty();
      }
      var file = Optional.ofNullable(objectMapper.readValue(response.body().string(), File.class));
      if (file.isEmpty() || !file.get().status().equals("processed")) {
        return Optional.empty();
      }
      return file;
    } catch (IOException failure) {
      log.error("Creating a thread caused an exception", failure);
      return Optional.empty();
    }
  }
}
