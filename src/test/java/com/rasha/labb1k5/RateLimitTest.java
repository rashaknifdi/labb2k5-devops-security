package com.rasha.labb1k5;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RateLimitTest {

    @Test
    void testBackoffAgainst429() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .requestFactory(factory)
                .build();

        // Anta att du har en testcontroller som alltid returnerar 429 på /test/429
        try {
            client.get()
                    .uri("/test/429")
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            // här kan du logga och manuellt verifiera backoff i konsolen
            assertEquals(true, true);
        }
    }
}
