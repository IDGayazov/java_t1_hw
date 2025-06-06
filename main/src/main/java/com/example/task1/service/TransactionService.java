package com.example.task1.service;

import com.example.task1.dto.ProcessedTransactionDto;
import com.example.task1.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> getAllTransactions();
    TransactionDto getTransactionById(Long id);
    ProcessedTransactionDto createTransaction(TransactionDto transactionDto);
    TransactionDto updateTransactionById(Long id, TransactionDto transactionDto);
    void deleteTransactionById(Long id);
}
