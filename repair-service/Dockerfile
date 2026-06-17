# Use a smaller Java 21 runtime image
FROM eclipse-temurin:21-jre-alpine

# Set working directory inside container
WORKDIR /app

# Copy the jar from target folder
COPY target/*.jar app.jar

# Keep defaults conservative; docker-compose can override JAVA_OPTS.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=65.0 -XX:InitialRAMPercentage=20.0 -XX:+ExitOnOutOfMemoryError"

# Run the jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
