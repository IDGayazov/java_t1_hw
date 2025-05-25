package com.example.task1.dto;

import java.math.BigDecimal;

public record TransactionDto (
        Long id,
        BigDecimal amount,
        Long accountId
) {
}
