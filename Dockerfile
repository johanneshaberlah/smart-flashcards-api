# Use the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application and Gradle files to the container
COPY . .

# Run Gradle to build the application
# Use the ARG values as part of the command
RUN ./gradlew build --no-daemon -x test

# Set the entrypoint command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "./build/libs/smart-flashcards-api-0.0.1.jar"]
