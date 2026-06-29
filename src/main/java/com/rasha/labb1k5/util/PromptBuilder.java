package com.rasha.labb1k5.util;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildSystemPrompt() {
        return """
                Du är en strikt JSON-generator.
                Du får ENDAST svara med en JSON-sträng som följer exakt detta schema:
                {
                  "sentiment": "positive" | "neutral" | "negative",
                  "confidence": 0-100
                }
                Du får inte använda markdown, inga backticks, ingen förklarande text.
                Du får inte lägga till extra fält.
                """;
    }

    public String buildUserPrompt(String userInput) {
        return "Analysera följande text och returnera JSON enligt schemat: " + userInput;
    }
}
