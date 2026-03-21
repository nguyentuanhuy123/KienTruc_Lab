# Stage 1: Build the application artifact
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create the final, lightweight runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080 # Adjust port as needed for your application
CMD ["java", "-jar", "app.jar"]
