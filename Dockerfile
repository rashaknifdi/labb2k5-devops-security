# ---------------------------------------------------------
# BUILD STAGE — bygger Spring Boot-applikationen
# ---------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Kopiera Maven-filer separat för att möjliggöra caching av dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Ladda ner dependencies offline (snabbare builds i CI/CD)
RUN ./mvnw dependency:go-offline

# Kopiera källkod
COPY src src

# Bygg applikationen (tester körs redan i CI)
RUN ./mvnw clean package -DskipTests


# ---------------------------------------------------------
# RUNTIME STAGE — minimal JRE-image för produktion
# ---------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiera färdigbyggd jar från build-steget
COPY --from=build /app/target/*.jar app.jar

# Exponera Spring Boot-porten
EXPOSE 8080

# Viktigt: JWT_SECRET måste skickas in via miljövariabel
# Exempel:
# docker run -e JWT_SECRET=hemligtvärde -p 8080:8080 min-app

# Starta applikationen med optimerad JVM-konfiguration
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:+UseStringDeduplication", "-jar", "app.jar"]
