package com.rasha.labb1k5.dto;

import jakarta.validation.constraints.NotBlank;

public class AiRequestDto {

    @NotBlank(message = "Text får inte vara tom")
    private String text;

    public AiRequestDto() {
    }

    public AiRequestDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
