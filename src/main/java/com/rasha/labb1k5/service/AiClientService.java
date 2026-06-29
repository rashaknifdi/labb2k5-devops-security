package com.rasha.labb1k5.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasha.labb1k5.dto.AiResponseDto;
import com.rasha.labb1k5.util.PromptBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AiClientService {

    private final String apiKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final PromptBuilder promptBuilder;

    public AiClientService(
            @Value("${openai.api.key}") String apiKey,
            Validator validator,
            PromptBuilder promptBuilder
    ) {
        this.apiKey = apiKey;
        this.validator = validator;
        this.promptBuilder = promptBuilder;
        this.objectMapper = new ObjectMapper();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(8000);

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .requestFactory(factory)
                .build();
    }

    @PostConstruct
    public void failFastIfMissingKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("CRITICAL: API key is missing.");
        }
    }

    public AiResponseDto analyzeSentiment(String userText) {

        System.out.println("ANALYZE SENTIMENT CALLED with text = " + userText);

        // ⭐ PromptBuilder används här
        String systemPrompt = promptBuilder.buildSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(userText);

        Map<String, Object> payload = Map.of(
                "model", "gpt-3.5-turbo",
                "temperature", 0.1,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        int retries = 3;
        long delay = 1000;

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                String rawBody = restClient.post()
                        .uri("")
                        .header("Authorization", "Bearer " + apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .body(String.class);

                Map<String, Object> root = objectMapper.readValue(rawBody, Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) message.get("content");

                try {
                    AiResponseDto dto = objectMapper.readValue(content, AiResponseDto.class);
                    Set<ConstraintViolation<AiResponseDto>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        return fallbackDto();
                    }
                    return dto;
                } catch (JsonProcessingException e) {
                    return fallbackDto();
                }

            } catch (RestClientResponseException ex) {

                System.out.println("OPENAI ERROR RESPONSE = " + ex.getResponseBodyAsString());
                System.out.println("STATUS = " + ex.getStatusCode().value());

                if (ex.getStatusCode().value() == 429 && attempt < retries) {
                    System.out.println("WARN: 429 rate limit, attempt " + attempt + ", waiting " + delay + "ms");
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ignored) {}
                    delay *= 2;
                    continue;
                }

                return fallbackDto();

            } catch (Exception e) {
                System.out.println("GENERIC ERROR = " + e.getMessage());
                return fallbackDto();
            }
        }

        return fallbackDto();
    }

    private AiResponseDto fallbackDto() {
        return new AiResponseDto("neutral", 50);
    }
}
