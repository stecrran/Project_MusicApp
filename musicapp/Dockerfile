# Multi-stage build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/MusicApp-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/logback-spring.xml logback-spring.xml
EXPOSE 9091
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dlogging.config=logback-spring.xml", "-jar", "/app/app.jar"]
