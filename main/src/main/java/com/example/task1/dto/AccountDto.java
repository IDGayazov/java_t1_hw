package com.example.task1.dto;

import java.math.BigDecimal;

public record AccountDto(
        Long id,
        BigDecimal balance,
        String accountType,
        Long clientId,
        String accountStatus,
        Long frozenAmount
) {
}
