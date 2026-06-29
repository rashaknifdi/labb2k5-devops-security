# -----------------------------
# Build stage: bygger Spring Boot-appen
# -----------------------------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Kopiera Maven-filer separat för att cachea dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Ladda ner dependencies offline (snabbare builds)
RUN ./mvnw dependency:go-offline

# Kopiera källkod
COPY src src

# Bygg applikationen (skipTests för snabbare Docker-build)
RUN ./mvnw clean package -DskipTests


# -----------------------------
# Run stage: minimal JRE-image
# -----------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiera färdigbyggd jar från build-steget
COPY --from=build /app/target/*.jar app.jar

# Exponera porten
EXPOSE 8080

# Starta applikationen
ENTRYPOINT ["java", "-jar", "app.jar"]
