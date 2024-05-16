# Use a base image with Gradle installed
FROM gradle:8.7.0-jdk17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the application and Gradle files to the container
COPY . .

# Run Gradle to build the application
RUN gradle build --no-daemon -x test

# Use a smaller base image for the final run
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/build/libs/smart-flashcards-api-0.0.1.jar /app/build/libs/smart-flashcards-api-0.0.1.jar

# Set the entrypoint command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/build/libs/smart-flashcards-api-0.0.1.jar"]
