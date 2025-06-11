package com.example.service;

import com.example.model.dto.TransactionDto;

public interface TransactionProcessService {
    void processTransaction(TransactionDto transactionDto);
}
