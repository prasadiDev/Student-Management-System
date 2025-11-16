# Multi-stage Dockerfile for building and running the Spring Boot application
# Stage 1: build the app using Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy Maven files first to leverage Docker layer caching
COPY pom.xml ./
COPY mvnw ./
RUN chmod +x mvnw

# Copy source
COPY src src

# Build the project (skip tests for faster image builds)
RUN mvn -DskipTests clean package


# Stage 2: run the packaged jar with a small JRE image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the previous stage. Adjust jar name if your artifactId/version differs.
ARG JAR_FILE=target/sms-0.0.1-SNAPSHOT.jar
COPY --from=build /workspace/${JAR_FILE} app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
