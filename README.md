# Individuell Labb 1k5: Robust AI-integration & Tillförlitligh 

## Översikt

Detta projekt är en Spring Boot‑applikation som fungerar som en säker och robust proxy mot en extern AI‑tjänst (OpenAI).  
Syftet är att bygga ett tillförlitligt lager runt AI‑anropet med fokus på:

-   säker nyckelhantering
-   timeouts
-   deterministisk prompt
-   rate‑limit‑hantering
-   validering
-   fallback
-   skydd mot hallucinationer


Projektet använder en sentimentanalys som exempel.

## Säker konfiguration

****API‑nyckeln hanteras via miljövariabel och aldrig i koden.****

Nyckeln injiceras med:

```java

@Value("${openai.api.key}")
```
Fail‑fast säkerställer att applikationen inte startar utan nyckel:

```java

@PostConstruct  
public void failFastIfMissingKey() {  
if (apiKey == null || apiKey.isBlank()) {  
throw new IllegalStateException("CRITICAL: API key is missing.");  
}  
}
```

Detta uppfyller kraven för säker credential‑hantering.

## Timeouts

För att skydda servern från långsamma AI‑svar används:

```java

factory.setConnectTimeout(2000);  
factory.setReadTimeout(8000);
```
Det innebär:

-   max 2 sekunder att ansluta
-   max 8 sekunder att vänta på svar

Detta förhindrar att trådar blockeras.

## Prompt Engineering

Systemprompten tvingar modellen att returnera ren ****JSON**** enligt ett bestämt schema.

-   Ingen markdown
-   Ingen extra text
-   Endast JSON
-   Temperatur satt till 0.1 för stabilitet

Exempel:

```java

String systemPrompt = """  
Du är en strikt JSON-generator.  
Du får ENDAST svara med en JSON-sträng som följer exakt detta schema:  
{  
"sentiment": "positive" | "neutral" | "negative",  
"confidence": 0-100  
}  
Du får inte använda markdown, inga backticks, ingen förklarande text.  
Du får inte lägga till extra fält.  
""";
```
Detta gör modellen deterministisk och förutsägbar.

## Rate Limits (Exponential Backoff)

Vid ****HTTP 429**** används exponential backoff:

-   3 försök
-   1s → 2s → 4s

```java

if (ex.getStatusCode().value() == 429) {  
Thread.sleep(delay);  
delay \*= 2;  
continue;  
}
```

Detta gör integrationen stabil även vid hög belastning.

## Hallucinationer & Validering

AI‑svar parsas och valideras innan de accepteras.

-   JSON parsas med ****ObjectMapper****
-   DTO valideras med ****Bean Validation****

Vid fel → fallback‑svar:

```java

return new AiResponseDto("neutral", 50);
```
Detta skyddar applikationen från trasiga eller oförutsägbara AI‑svar.

## API‑endpoint

Applikationen exponerar ett ****POST‑endpoint****:

Kod

POST /api/ai/sentiment

Exempel‑body:

```json

{  
"text": "Jag är väldigt glad idag!"  
}
```
Exempel‑svar (om AI‑anropet lyckas):

```json

{  
"sentiment": "positive",  
"confidence": 90  
}
```

Fallback‑svar (timeout, rate‑limit, trasig JSON etc.):

```json

{  
"sentiment": "neutral",  
"confidence": 50  
}
```

## Arkitektur

-   ****AiController**** tar emot request
-   ****AiClientService**** hanterar AI‑anrop, timeouts, backoff, validering och fallback
-   ****PromptBuilder**** genererar deterministiska prompts
-   ****DTOs**** definierar strikt input/output
-   ****RestClient**** används för HTTP‑anrop

## Tillförlitlighet

Systemet är byggt för att alltid svara, även om OpenAI inte gör det.

-   fallback‑läge
-   validering
-   timeouts
-   retry/backoff
-   strikt JSON‑schema

Detta gör integrationen robust och produktionslik.

## Körning

### 1\. Sätt miljövariabel:

Kod

OPENAI\_API\_KEY=sk-xxxxxxx

### 2\. Starta applikationen

Spring Boot körs lokalt på:

Kod

http://localhost:8080

### 3\. Testa endpointen i Postman

****POST****

Kod

http://localhost:8080/api/ai/sentiment

****Headers:****

Kod

Content-Type: application/json

****Body:****

```json

{  
"text": "Jag är väldigt glad idag!"  
}
```

## Tester (Kantfall & Tillförlitlighet)
Projektet innehåller tre enhetstester som verifierar tillförlitlighetslagret enligt Steg 6 i labbinstruktionen.

### Hallucinationstest
Testar att trasig eller icke‑JSON text från AI fångas upp och att fallback‑värden returneras.

Scenario:  
AI‑svar: "Sure, here is your summary ..."  
Förväntat resultat:  
Fallback → { "sentiment": "neutral", "confidence": 50 }

### Rate‑limit‑test (HTTP 429)
Testar att klienten hanterar rate‑limit‑fel utan att krascha och att exponential backoff triggas.

Scenario:  
En test‑endpoint returnerar alltid 429
Förväntat resultat:  
Backoff‑logik körs, inget ohanterat undantag kastas

### Timeout‑test
Testar att klienten avbryter långsamma anrop korrekt.

Scenario:  
ReadTimeout sätts till 10 ms
Anrop görs mot en tjänst som svarar efter 5 sekunder
Förväntat resultat:  
Timeout‑exception kastas

Dessa tester verifierar att systemet hanterar:

långsamma AI‑svar

rate limits

trasig eller oförutsägbar AI‑output

…och att fallback‑logiken fungerar som avsett.

### Köra alla tester
Du kan köra alla tester i projektet via Maven i IntelliJ Terminal:

```bash
mvn test
```
Detta kör samtliga tester i src/test/java, inklusive:

timeout‑test

rate‑limit‑test

hallucinationstest

## Sammanfattning

Detta projekt uppfyller alla krav i Labb 1k5:

-   säker nyckelhantering
-   robust AI‑integration
-   deterministisk prompt
-   rate‑limit‑skydd
-   validering och fallback
-   tydlig arkitektur
-   tillförlitlighetsrapport