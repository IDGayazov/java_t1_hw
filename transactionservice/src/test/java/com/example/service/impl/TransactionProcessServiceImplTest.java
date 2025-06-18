package com.example.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.model.TransactionResult;
import com.example.model.dto.TransactionDto;
import com.example.model.enums.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionProcessServiceImplTest {

    @Mock
    private KafkaTemplate<String, TransactionResult> kafkaTemplate;

    @InjectMocks
    private TransactionProcessServiceImpl transactionProcessService;

    @Captor
    private ArgumentCaptor<TransactionResult> transactionResultCaptor;

    private final String testTopic = "transactions-result-topic";
    private final int maxCount = 5;
    private final long timeWindow = 60;

    @BeforeEach
    void setUp() {
        transactionProcessService.setTransactionsResultTopic(testTopic);
        transactionProcessService.setMaxTransactionCount(maxCount);
        transactionProcessService.setTimeWindowSeconds(timeWindow);
    }

    @Test
    void processTransaction_WhenSufficientFunds_ShouldSendAcceptedResult() {
        TransactionDto dto = new TransactionDto(
                1L, 1L, 1L,
                LocalDateTime.now(),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200));

        transactionProcessService.processTransaction(dto);

        verify(kafkaTemplate).send(eq(testTopic), transactionResultCaptor.capture());
        TransactionResult result = transactionResultCaptor.getValue();
        assertEquals(TransactionStatus.ACCEPTED, result.getStatus());
        assertEquals("Transaction accepted", result.getMessage());
    }

    @Test
    void processTransaction_WhenInsufficientFunds_ShouldSendRejectedResult() {
        TransactionDto dto = new TransactionDto(
                1L, 1L, 1L,
                LocalDateTime.now(),
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(200));

        transactionProcessService.processTransaction(dto);

        verify(kafkaTemplate).send(eq(testTopic), transactionResultCaptor.capture());
        TransactionResult result = transactionResultCaptor.getValue();
        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Insufficient funds", result.getMessage());
    }

    @Test
    void checkTransactionLimit_WhenUnderLimit_ShouldReturnFalse() {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < maxCount - 1; i++) {
            TransactionDto dto = new TransactionDto(
                    1L, 1L, (long) i,
                    now.minusSeconds(30),
                    BigDecimal.valueOf(50),
                    BigDecimal.valueOf(1000));
            transactionProcessService.processTransaction(dto);
        }

        TransactionDto newTransaction = new TransactionDto(
                1L, 1L, 100L,
                now,
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(1000));

        boolean result = transactionProcessService.checkTransactionLimit(1L, newTransaction);

        assertFalse(result);
    }

    @Test
    void checkTransactionLimit_WhenOldTransactions_ShouldIgnoreThem() {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < maxCount; i++) {
            TransactionDto dto = new TransactionDto(
                    1L, 1L, (long) i,
                    now.minusSeconds(timeWindow + 10),
                    BigDecimal.valueOf(50),
                    BigDecimal.valueOf(1000));
            transactionProcessService.processTransaction(dto);
        }

        TransactionDto newTransaction = new TransactionDto(
                1L, 1L, 100L,
                now,
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(1000));

        boolean result = transactionProcessService.checkTransactionLimit(1L, newTransaction);

        assertFalse(result);
    }

    @Test
    void blockTransactions_ShouldBlockOnlyRecentTransactions() {
        LocalDateTime now = LocalDateTime.now();
        ConcurrentHashMap<Long, Queue<TransactionDto>> store =
                (ConcurrentHashMap<Long, Queue<TransactionDto>>) transactionProcessService.getTransactionStore();

        Queue<TransactionDto> transactions = new LinkedList<>();
        transactions.add(new TransactionDto(
                1L, 1L, 1L,
                now.minusSeconds(timeWindow + 10),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(1000)));
        transactions.add(new TransactionDto(
                1L, 1L, 2L,
                now.minusSeconds(30),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(1000)));
        store.put(1L, transactions);

        transactionProcessService.blockTransactions(1L);

        verify(kafkaTemplate, times(1)).send(eq(testTopic), any(TransactionResult.class));
    }

    @Test
    void addTransactionToStore_ShouldMaintainQueueSize() {
        LocalDateTime now = LocalDateTime.now();
        TransactionDto dto = new TransactionDto(
                1L, 1L, 1L,
                now,
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(1000));

        transactionProcessService.addTransactionToStore(dto);

        assertTrue(transactionProcessService.getTransactionStore().containsKey(1L));
        assertEquals(1, transactionProcessService.getTransactionStore().get(1L).size());
    }
}