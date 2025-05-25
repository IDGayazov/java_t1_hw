package com.example.task1.service;

import com.example.task1.dto.TransactionDto;

public interface TransactionService {
    TransactionDto getTransactionById(Long id);
    TransactionDto createTransaction(TransactionDto transactionDto);
    TransactionDto updateTransactionById(Long id, TransactionDto transactionDto);
    void deleteTransactionById(Long id);
}
