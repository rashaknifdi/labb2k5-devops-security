package com.rasha.labb1k5;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimeoutTest {

    @Test
    void testTimeoutHappens() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(10);

        assertThrows(Exception.class, () -> {
            var request = factory.createRequest(
                    new java.net.URI("https://httpstat.us/200?sleep=5000"),
                    org.springframework.http.HttpMethod.GET
            );
            request.execute();
        });
    }
}
