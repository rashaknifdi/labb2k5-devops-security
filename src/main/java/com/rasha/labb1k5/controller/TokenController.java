package com.rasha.labb1k5.controller;

import com.rasha.labb1k5.security.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @GetMapping("/token")
    public String getToken() {
        // Skapa en token för en test-användare
        return JwtUtil.generateToken("rasha");
    }
}
