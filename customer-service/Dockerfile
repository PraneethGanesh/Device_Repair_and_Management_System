# Use Java 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the jar from target folder
COPY target/*.jar app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]