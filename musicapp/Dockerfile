# Start from an official JDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy built JAR into the image (replace with actual JAR name or use wildcard in CI)
COPY target/MusicApp-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (adjust if needed)
EXPOSE 9091

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
