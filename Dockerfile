FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/ExpenceTracker-0.0.1-SNAPSHOT.jar ExpenceTracker.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "ExpenceTracker.jar"]