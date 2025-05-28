FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build aplikacji
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Kopiuj zbudowany JAR
COPY --from=builder /app/target/bookstoreProject-1.0-SNAPSHOT.jar app.jar

# Ekspozycja portu
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]