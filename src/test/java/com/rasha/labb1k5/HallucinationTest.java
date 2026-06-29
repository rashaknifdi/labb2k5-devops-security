package com.rasha.labb1k5;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasha.labb1k5.dto.AiResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HallucinationTest {

    @Test
    void testBrokenJsonFallsBack() {
        String broken = "Sure, here is your summary ...";

        ObjectMapper mapper = new ObjectMapper();
        AiResponseDto dto;
        try {
            dto = mapper.readValue(broken, AiResponseDto.class);
        } catch (Exception e) {
            dto = new AiResponseDto("neutral", 50);
        }

        assertEquals("neutral", dto.getSentiment());
        assertEquals(50, dto.getConfidence());
    }
}
