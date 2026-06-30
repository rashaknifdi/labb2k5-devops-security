package com.rasha.labb1k5.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasha.labb1k5.dto.AiResponseDto;
import com.rasha.labb1k5.util.PromptBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final Validator validator;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private volatile RestClient restClient; // lazy init

    public AiClientService(
            @Value("${openai.api.key:}") String apiKey,
            @Autowired(required = false) Validator validator,
            @Autowired(required = false) PromptBuilder promptBuilder
    ) {
        this.apiKey = apiKey != null ? apiKey : "";
        this.validator = validator;
        this.promptBuilder = promptBuilder;

        if (this.apiKey.isBlank()) {
            System.out.println("WARN: No OpenAI API key provided. AI features disabled.");
        }
    }

    private RestClient getClient() {
        if (restClient == null) {
            synchronized (this) {
                if (restClient == null) {

                    if (apiKey.isBlank()) {
                        return null; // ⭐ CI/test fallback
                    }

                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(2000);
                    factory.setReadTimeout(8000);

                    restClient = RestClient.builder()
                            .baseUrl("https://api.openai.com/v1/chat/completions")
                            .requestFactory(factory)
                            .build();
                }
            }
        }
        return restClient;
    }

    public AiResponseDto analyzeSentiment(String userText) {

        if (apiKey.isBlank() || promptBuilder == null) {
            return fallbackDto(); // ⭐ CI/test fallback
        }

        RestClient client = getClient();
        if (client == null) {
            return fallbackDto(); // ⭐ extra säkerhet
        }

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

        try {
            String rawBody = client.post()
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

            AiResponseDto dto = objectMapper.readValue(content, AiResponseDto.class);
            
            if (validator != null) {
                Set<ConstraintViolation<AiResponseDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    return fallbackDto();
                }
            }

            return dto;

        } catch (Exception e) {
            return fallbackDto();
        }
    }

    private AiResponseDto fallbackDto() {
        return new AiResponseDto("neutral", 50);
    }
}
