package com.example.task1.service.impl;

import com.example.task1.annotation.LoggingException;
import com.example.task1.annotation.Metric;
import com.example.task1.dto.ProcessedTransactionDto;
import com.example.task1.dto.TransactionDto;
import com.example.task1.entity.Account;
import com.example.task1.entity.Transaction;
import com.example.task1.entity.enums.AccountStatus;
import com.example.task1.entity.enums.TransactionStatus;
import com.example.task1.mapper.TransactionMapper;
import com.example.task1.repository.AccountRepository;
import com.example.task1.repository.TransactionRepository;
import com.example.task1.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@LoggingException
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Override
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
        return transactionMapper.toDto(transaction);
    }

    @Metric
    @Override
    public ProcessedTransactionDto createTransaction(TransactionDto transactionDto) {
        Account account = accountRepository.findById(transactionDto.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (!AccountStatus.OPEN.equals(account.getAccountStatus())) {
            throw new IllegalStateException("Account is not in OPEN status");
        }

        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setAccount(account);
        transaction.setTime(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.REQUESTED);

        account.setBalance(account.getBalance().add(transactionDto.amount()));
        accountRepository.save(account);

        Transaction savedTransaction = transactionRepository.save(transaction);

        ProcessedTransactionDto processedTransactionDto = new ProcessedTransactionDto(
                savedTransaction.getAccount().getClient().getClientId(),
                account.getAccountId(),
                savedTransaction.getId(),
                savedTransaction.getTime(),
                savedTransaction.getAmount(),
                savedTransaction.getAccount().getBalance()
        );

        log.info("Transaction created for account {} with new balance {}",
                account.getId(), account.getBalance());

        return processedTransactionDto;
    }

    @Metric
    @Override
    public TransactionDto updateTransactionById(Long id, TransactionDto transactionDto) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Transaction not found with id: " + id)
        );

        if(transactionDto.accountId() != null){
            Account account = accountRepository.findById(transactionDto.accountId()).orElseThrow(
                    () -> new EntityNotFoundException("Account not found with id: " + transactionDto.accountId())
            );
            transaction.setAccount(account);
        }

        Optional.ofNullable(transactionDto.amount()).ifPresent(transaction::setAmount);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        log.info("Updated transaction with id: {}", id);
        return transactionMapper.toDto(updatedTransaction);
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
