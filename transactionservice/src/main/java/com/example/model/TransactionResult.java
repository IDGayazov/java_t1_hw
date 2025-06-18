package com.example.model;

import com.example.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResult {
    private Long accountId;
    private Long transactionId;
    private TransactionStatus status;
    private String message;
}
