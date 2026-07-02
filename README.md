# **Individuell Labb 2k5 – DevOps, CI/CD & Applikationssäkerhet**

### *Spring Boot + Docker + GitHub Actions*

## **Översikt**

Detta projekt är en säkerhetsförstärkt Spring Boot‑applikation som analyserar sentiment via ett AI‑API. I denna labb har jag implementerat:

-   Containerisering med Docker

-   CI/CD‑pipeline med GitHub Actions

-   Automatiska tester

-   Automatiserad Docker‑deployment

-   **JWT**‑baserad autentisering

-   **OWASP** Dependency‑Check

-   Åtgärder för tre **OWASP** Top 10‑sårbarheter

## **API‑endpoints**

| Endpoint | Metod | Beskrivning |
| --- | --- | --- |
| /token | GET | Genererar en JWT‑token |
| /api/ai/sentiment | POST | Analyserar sentiment (kräver JWT) |

### **Exempelanrop**

#### Hämta token

http

```
GET /token
```

#### Använd token

http

```
POST /api/ai/sentiment
Authorization: Bearer <token>
Content-Type: application/json

{
  "text": "Jag älskar soliga dagar!"
}
```

## **Säkerhet**

### **JWT‑autentisering**

-   `/token` är öppet

-   `/api/ai/sentiment` kräver **JWT**

-   Secret key via `JWT_SECRET` environment variable

-   Token valideras i `JwtAuthFilter`

-   SecurityContext sätts korrekt

###  **Input‑validering**

DTO‑klasser använder `jakarta.validation`:

java

```
@NotBlank
private String text;

@Min(0)
@Max(100)
private Integer confidence;
```

### **OWASP Dependency‑Check**

-   Integrerad i `pom.xml`

-   Körs automatiskt i CI

-   **CVE**‑databas cacheas

-   Build failar vid **CVSS ≥ 7**

## **DevOps – CI/CD Pipeline**

Pipeline består av två jobb:

### **1\. build-and-test (CI)**

Steg:

-   Checkout

-   Java 21

-   Install Maven

-   Cache (~/.m2)

-   Cache Dependency‑Check DB

-   `mvn test`

-   `mvn dependency-check:check`

**Resultat:** Automatiska tester + säkerhetsskanning.

### **2\. docker-push (CD)**

Steg:

-   Checkout

-   Buildx setup

-   Login till Docker Hub (GitHub Secrets)

-   Build & push Docker image

**Resultat:** Fullt automatiserad deployment.

## **Docker**

### Multi‑stage Dockerfile

-   **Stage 1:** Maven bygger jar

-   **Stage 2:** Temurin 21‑jre‑alpine kör jar

### Bygga lokalt

bash

```
docker build -t labb2k5 .
```

### Köra lokalt

bash

```
docker run -p 8080:8080 -e JWT_SECRET=hemligt labb2k5
```

## **Miljövariabler**

| Variabel | Beskrivning |
| --- | --- |
| JWT_SECRET | Hemlig nyckel för signering av JWT |
| OPENAI_API_KEY | API‑nyckel för AI‑anrop (valfri i CI) |

## **Identifierade & Åtgärdade Sårbarheter (OWASP Top 10)**

### ✔ **A01 – Broken Access Control**

-   Endpoints skyddade med **JWT**

-   Filterkedja implementerad

### ✔ **A07 – Identification & Authentication Failures**

-   **JWT**‑baserad autentisering

-   Secret via environment variable

-   Token valideras korrekt

### ✔ **A06 – Vulnerable Components**

-   Dependency‑Check integrerad

-   CI stoppar vid kritiska **CVE**:er

-   **CVE**‑databas cacheas

## **Testning**

Projektet innehåller automatiska tester som körs i CI:

-   Spring Boot context test

-   Rate limit test

-   Timeout test

-   Hallucination test

-   Fallback test när **API**‑nyckel saknas

**Alla tester passerar i CI.**

## **Kravuppfyllnad**

-   ✔ Containerisering

-   ✔ CI med automatiska tester

-   ✔ CD med Docker‑push

-   ✔ Secrets via GitHub Secrets

-   ✔ **JWT**‑autentisering

-   ✔ **OWASP** Dependency‑Check

-   ✔ Tre **OWASP**‑sårbarheter identifierade och åtgärdade

-   ✔ Skriftlig säkerhetsrapport

-   ✔ Reproducerbar DevOps‑pipeline

## **Författare**

**Rasha Knifdi** 
Fullstack Java – Chas Academy