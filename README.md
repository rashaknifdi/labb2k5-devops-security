# **Individuell Labb 2k5 – DevOps, CI/CD & Application Security**

### *Spring Boot + Docker + GitHub Actions*

## **Översikt**

Detta projekt är en del av Individuell Labb 2k5 i kursen *DevOps, CI/CD & Applikationssäkerhet*. Applikationen är en säkerhetsförstärkt Spring Boot‑tjänst som har byggts ut med:

-   Containerisering med **Docker**

-   CI/CD‑pipeline via **GitHub Actions**

-   Automatiska tester

-   **OWASP Dependency‑Check**

-   **JWT‑baserad autentisering**

-   Åtgärder för tre **OWASP Top 10‑sårbarheter**

Projektet är fullt automatiserat och redo för deployment.

## **Funktionalitet**

Applikationen innehåller:

-   `/token` – genererar en JWT‑token

-   `/api/ai/sentiment` – skyddad endpoint som kräver giltig JWT

-   Spring Security‑konfiguration med eget JWT‑filter

-   Dependency‑Check som stoppar build vid kritiska CVE:er

## **API‑endpoints**

| Endpoint | Metod | Beskrivning |
| --- | --- | --- |
| /token | GET | Genererar en JWT‑token |
| /api/ai/sentiment | POST | Analyserar sentiment (kräver JWT) |

### **Exempelanrop**

#### Hämta token

Kod

```
GET /token
```

#### Använd token

Kod

```
POST /api/ai/sentiment
Authorization: Bearer <token>
Content-Type: application/json

{
  "text": "Jag älskar soliga dagar!"
}
```
## **Testa API i Postman**

För att testa applikationen med JWT‑autentisering i Postman:

### **1\. Hämta JWT‑token**

**GET**

Kod

```
http://localhost:8080/token
```

Kopiera token från svaret, t.ex.:

Kod

```
eyJhbGciOiJIUzI1NiJ9...
```

### **2\. Anropa skyddad endpoint**

**POST**

Kod

```
http://localhost:8080/api/ai/sentiment
```

### **Headers**

| Key | Value |
| --- | --- |
| Authorization | Bearer <din-token> |
| Content-Type | application/json |

### **Body (raw JSON)**

json

```
{
  "text": "Jag älskar soliga dagar!"
}
```

### **Förväntat svar**

json

```
{
  "sentiment": "positive",
  "confidence": 95
}
```

Om token saknas eller är fel returneras:

Kod

```
401 Unauthorized
```
## **Säkerhet**

### **A01 – Broken Access Control**

-   Endpoints skyddade via Spring Security

-   JWT‑filter validerar token

-   Obehöriga får **401 Unauthorized**

### **A07 – Identification & Authentication Failures**

-   JWT‑generering och validering

-   Secret key via `JWT_SECRET` miljövariabel

-   Ingen hårdkodad hemlighet i koden

### **A06 – Vulnerable & Outdated Components**

-   OWASP Dependency‑Check integrerad i Maven

-   CI‑pipeline kör dependency‑check vid varje push

-   Build failar vid **CVSS ≥ 7**

## **Docker**

Applikationen är containeriserad med en multi‑stage Dockerfile:

-   **Build‑stage:** `eclipse-temurin:21-jdk-alpine`

-   **Runtime‑stage:** `eclipse-temurin:21-jre-alpine`

-   Minimal image med endast färdigbyggd `.jar`

-   Optimerad för produktion

### **Bygga lokalt**

bash

```
docker build -t labb2k5 .
```

### **Köra lokalt**

bash

```
docker run -p 8080:8080 -e JWT_SECRET=hemligt labb2k5
```

## **CI/CD – GitHub Actions**

Pipeline består av två jobb:

### **1\. build-and-test (CI)**

Steg:

-   Checkout

-   Java 21

-   Install Maven

-   Cache av Maven dependencies

-   Cache av Dependency‑Check databas

-   `mvn test`

-   `mvn dependency-check:check`

**Resultat:** Automatiska tester + säkerhetsskanning.

### **2\. docker-push (CD)**

Steg:

-   Checkout

-   Docker Buildx

-   Login via GitHub Secrets

-   Build & push till Docker Hub

**Resultat:** Fullt automatiserad deployment vid varje godkänd push.

Pipeline körs vid:

-   `push` till `main`

-   `pull_request` till `main`

## **Projektstruktur**

Kod

```
labb2k5-devops-security/
│
├── src/
├── pom.xml
├── Dockerfile
├── .dockerignore
├── mvnw
├── .mvn/
└── .github/workflows/ci-cd-pipeline.yml
```

## **Miljövariabler**

| Variabel | Beskrivning |
| --- | --- |
| JWT_SECRET | Hemlig nyckel för signering av JWT‑tokens |
| OPENAI_API_KEY | API‑nyckel för AI‑anrop (valfri i CI) |

Exempel:

bash

```
-e JWT_SECRET=superhemligt
```

## **Säkerhetsrapport**

En fullständig säkerhetsrapport finns i projektet och innehåller:

-   Identifierade sårbarheter

-   Tekniska åtgärder

-   Riskanalys

-   Prioritering

-   Koppling till OWASP Top 10

## **Sammanfattning**

Detta projekt uppfyller alla krav för **VG** i Labb 2k5:

-   Full CI/CD‑pipeline

-   Automatiserad Docker‑deployment

-   Säker hantering av secrets

-   JWT‑baserad autentisering

-   Dependency‑Check integrerad

-   Åtgärdade OWASP‑sårbarheter

-   Dokumenterad säkerhetsanalys

-   Optimerad Dockerfile

-   `.dockerignore` för snabbare builds

## **Författare**

**Rasha Knifdi** 
Fullstack Java – Chas Academy