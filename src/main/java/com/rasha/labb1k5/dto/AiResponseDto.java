package com.rasha.labb1k5.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AiResponseDto {

    @NotBlank
    private String sentiment; // "positive", "neutral", "negative"

    @NotNull
    @Min(0)
    @Max(100)
    private Integer confidence; // 0–100

    public AiResponseDto() {
    }

    public AiResponseDto(String sentiment, Integer confidence) {
        this.sentiment = sentiment;
        this.confidence = confidence;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }
}
