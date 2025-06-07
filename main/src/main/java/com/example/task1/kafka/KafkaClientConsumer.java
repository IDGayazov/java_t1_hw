package com.example.task1.kafka;

import com.example.task1.dto.ProcessedTransactionDto;
import com.example.task1.dto.TransactionDto;
import com.example.task1.entity.Account;
import com.example.task1.entity.Transaction;
import com.example.task1.entity.enums.AccountStatus;
import com.example.task1.entity.enums.TransactionStatus;
import com.example.task1.repository.AccountRepository;
import com.example.task1.repository.TransactionRepository;
import com.example.task1.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientConsumer {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private final TransactionService transactionService;
    private final KafkaTemplate<String, ProcessedTransactionDto> kafkaTemplate;

    @KafkaListener(id = "${kafka.consumer.group-id}",
            topics = {"${kafka.consumer.topic.transactions-topic}"},
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload TransactionDto message,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            ProcessedTransactionDto processedTransaction = transactionService.createTransaction(message);
            kafkaTemplate.send("t1_demo_transaction_accept", processedTransaction);
            ack.acknowledge();
            log.info("Transaction processed successfully: {}", processedTransaction);
        } catch (EntityNotFoundException e) {
            log.error("Account not found: {}", e.getMessage());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage());
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.consumer.transactions-accept}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransactionResult(@Payload Transaction result) {
        log.info("Received transaction result: {}", result);

        switch (result.getStatus()) {
            case ACCEPTED -> handleAccepted(result);
            case BLOCKED -> handleBlocked(result);
            case REJECTED -> handleRejected(result);
            default -> log.warn("Unknown status: {}", result.getStatus());
        }
    }

    @Transactional
    private void handleAccepted(Transaction result) {
        transactionRepository.findById(result.getTransactionId())
                .ifPresent(transaction -> {
                    transaction.setStatus(TransactionStatus.ACCEPTED);
                    transactionRepository.save(transaction);
                    log.info("Transaction {} accepted", result.getTransactionId());
                });
    }

    @Transactional
    private void handleBlocked(Transaction result) {
        transactionRepository.findById(result.getTransactionId())
                .ifPresent(transaction -> {
                    // Обновляем статус транзакции
                    transaction.setStatus(TransactionStatus.BLOCKED);
                    transactionRepository.save(transaction);

                    // Блокируем счет и замораживаем средства
                    Account account = transaction.getAccount();
                    account.setAccountStatus(AccountStatus.BLOCKED);

                    BigDecimal newFrozenAmount = account.getFrozenAmount() != null
                            ? account.getFrozenAmount().add(transaction.getAmount())
                            : transaction.getAmount();

                    account.setFrozenAmount(newFrozenAmount);
                    accountRepository.save(account);

                    log.info("Transaction {} blocked. Account {} frozen. Frozen amount: {}",
                            result.getTransactionId(), account.getId(), newFrozenAmount);
                });
    }

    @Transactional
    private void handleRejected(Transaction result) {
        transactionRepository.findById(result.getTransactionId())
                .ifPresent(transaction -> {
                    // Обновляем статус транзакции
                    transaction.setStatus(TransactionStatus.REJECTED);
                    transactionRepository.save(transaction);

                    // Возвращаем средства на счет
                    Account account = transaction.getAccount();
                    account.setBalance(account.getBalance().add(transaction.getAmount()));
                    accountRepository.save(account);

                    log.info("Transaction {} rejected. Balance returned for account {}",
                            result.getTransactionId(), account.getId());
                });
    }
}
