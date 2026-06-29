package com.rasha.labb1k5.controller;

import com.rasha.labb1k5.dto.AiRequestDto;
import com.rasha.labb1k5.dto.AiResponseDto;
import com.rasha.labb1k5.service.AiClientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiClientService aiClientService;

    public AiController(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

    @PostMapping("/sentiment")
    public AiResponseDto sentiment(@RequestBody @Valid AiRequestDto request) {
        return aiClientService.analyzeSentiment(request.getText());
    }
}
