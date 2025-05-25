package com.example.task1.service.impl;

import com.example.task1.dto.TransactionDto;
import com.example.task1.entity.Account;
import com.example.task1.entity.Transaction;
import com.example.task1.mapper.TransactionMapper;
import com.example.task1.repository.AccountRepository;
import com.example.task1.repository.TransactionRepository;
import com.example.task1.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
        return transactionMapper.toDto(transaction);
    }

    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setTime(LocalDateTime.now());
        log.info("Made transaction for account: {}", transaction.getAccount());
        return transactionMapper.toDto(transaction);
    }

    @Override
    public TransactionDto updateTransactionById(Long id, TransactionDto transactionDto) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Transaction not found with id: " + id)
        );

        Account account = accountRepository.findById(transactionDto.accountId()).orElseThrow(
                () -> new EntityNotFoundException("Account not found with id: " + transactionDto.accountId())
        );

        transaction.setAccount(account);
        transaction.setAmount(transactionDto.amount());

        log.info("Updated transaction with id: {}", id);
        return transactionMapper.toDto(transaction);
    }

    @Override
    public void deleteTransactionById(Long id) {
        if(!transactionRepository.existsById(id)){
            throw new EntityNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
        log.info("Deleted transaction with id: {}", id);
    }
}
