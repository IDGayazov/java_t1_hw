package com.example.service.impl;

import com.example.model.Account;
import com.example.model.Transaction;
import com.example.model.TransactionResult;
import com.example.model.dto.TransactionDto;
import com.example.model.enums.TransactionStatus;
import com.example.repository.AccountRepository;
import com.example.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionProcessServiceImpl {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, TransactionResult> kafkaTemplate;

    @Value("${transaction.limits.max-count}")
    private int maxTransactionCount;

    @Value("${transaction.limits.time-window}")
    private long timeWindowSeconds;

    @Transactional
    public void processTransaction(TransactionDto transactionDto) {
        Account account = accountRepository.findById(transactionDto.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // Проверка 1: Лимит количества транзакций
        if (checkTransactionLimit(account, transactionDto)) {
            blockTransactions(account);
            return;
        }

        // Проверка 2: Достаточно ли средств
        if (transactionDto.transactionAmount().compareTo(account.getBalance()) > 0) {
            sendRejectedResult(transactionDto, "Insufficient funds");
            return;
        }

        // Успешная обработка
        updateTransactionStatus(transactionDto.transactionId(), TransactionStatus.ACCEPTED);
        sendAcceptedResult(transactionDto);
    }

    private boolean checkTransactionLimit(Account account, TransactionDto currentTransaction) {
        LocalDateTime startTime = LocalDateTime.now().minusSeconds(timeWindowSeconds);

        long transactionCount = transactionRepository.countByAccountAndTimeAfter(
                account,
                startTime
        );

        return transactionCount >= maxTransactionCount &&
                currentTransaction.timestamp().isAfter(startTime);
    }

    private void blockTransactions(Account account) {
        LocalDateTime startTime = LocalDateTime.now().minusSeconds(timeWindowSeconds);

        List<Transaction> recentTransactions = transactionRepository
                .findByAccountAndTimeAfterAndStatus(
                        account,
                        startTime,
                        TransactionStatus.REQUESTED
                );

        recentTransactions.forEach(transaction -> {
            transaction.setStatus(TransactionStatus.BLOCKED);
            transactionRepository.save(transaction);

            sendBlockedResult(transaction);
        });
    }

    private void sendBlockedResult(Transaction transaction) {
        TransactionResult result = new TransactionResult(
                transaction.getAccount().getId(),
                transaction.getId(),
                TransactionStatus.BLOCKED,
                "Transaction blocked: limit exceeded"
        );
        kafkaTemplate.send("t1_demo_transaction_result", result);
    }

    private void sendRejectedResult(TransactionDto dto, String reason) {
        updateTransactionStatus(dto.transactionId(), TransactionStatus.REJECTED);

        TransactionResult result = new TransactionResult(
                dto.accountId(),
                dto.transactionId(),
                TransactionStatus.REJECTED,
                reason
        );
        kafkaTemplate.send("t1_demo_transaction_result", result);
    }

    private void sendAcceptedResult(TransactionDto dto) {
        TransactionResult result = new TransactionResult(
                dto.accountId(),
                dto.transactionId(),
                TransactionStatus.ACCEPTED,
                "Transaction accepted"
        );
        kafkaTemplate.send("t1_demo_transaction_result", result);
    }

    private void updateTransactionStatus(Long transactionId, TransactionStatus status) {
        transactionRepository.findById(transactionId).ifPresent(transaction -> {
            transaction.setStatus(status);
            transactionRepository.save(transaction);
        });
    }
}
