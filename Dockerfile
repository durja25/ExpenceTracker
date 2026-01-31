# --- BUILD STAGE ---
# Use an official Maven image with a JDK as the build environment.
FROM maven:3.9.6-openjdk-21-slim AS build

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy the pom.xml file first to allow Docker to cache dependencies separately from source code changes
COPY pom.xml .

# Download project dependencies (this layer is cached)
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the application into an executable JAR file
RUN mvn clean package -DskipTests

# --- PACKAGE STAGE ---
# Use a smaller JRE (Java Runtime Environment) image for the final runtime
FROM openjdk:21-jre-slim

# Set the working directory for the final image
WORKDIR /app

COPY --from=build /usr/src/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]




FROM eclipse-temurin:21-jre
WORKDIR /app


# Copy the JAR file from the 'build' stage into the final image
COPY target/ExpenceTracker-0.0.1-SNAPSHOT.jar ExpenceTracker.jar

# Expose the port your application runs on (commonly 8080 for web apps)
EXPOSE 8080

# Define the command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "ExpenceTracker.jar"]