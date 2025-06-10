package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class ClientCheckController {

    @PostMapping("/check-client")
    public ResponseEntity<ClientCheckResponse> checkClient(
            @RequestBody ClientCheckRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        // Аутентификация
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Проверка в черном списке (пример с рандомом)
        boolean isBlocked = new Random().nextInt(100) < 5; // 5% вероятность блокировки

        return ResponseEntity.ok(new ClientCheckResponse(isBlocked));
    }

    private boolean validateToken(String authHeader) {
        // Реализация проверки токена
        return authHeader != null && authHeader.startsWith("Bearer ")
                && isValidToken(authHeader.substring(7));
    }
}
