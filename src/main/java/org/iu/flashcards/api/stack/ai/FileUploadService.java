package org.iu.flashcards.api.stack.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileUploadService {
  private static final String API_URL = "https://api.openai.com/v1/files";

  private final ObjectMapper objectMapper;

  public FileUploadService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public File upload(MultipartFile multipartFile) throws IOException {
    OkHttpClient client = new OkHttpClient();

    // Create the request body with the file
    RequestBody fileBody = RequestBody.create(
      MediaType.parse(multipartFile.getContentType()),
      multipartFile.getBytes()
    );

    // Build the multipart request body
    MultipartBody requestBody = new MultipartBody.Builder()
      .setType(MultipartBody.FORM)
      .addFormDataPart("file", multipartFile.getOriginalFilename(), fileBody)
      .addFormDataPart("purpose", "assistants")
      .build();

    // Build the request
    Request request = new Request.Builder()
      .url(API_URL)
      .header("Authorization", "Bearer sk-proj-jY7i8V24As9sPfNL6uDfT3BlbkFJIcLqY7xdSnH6lUpLjbI0")
      .post(requestBody)
      .build();

    // Execute the request
    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) {
      throw new IOException("Unexpected code " + response.body().string());
    }
    return objectMapper.readValue(response.body().string(), File.class);
  }
}
