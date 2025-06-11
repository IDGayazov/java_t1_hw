package com.example.dto;

public record ClientCheckRequest(
        Long accountId,
        Long clientId
) {
}
