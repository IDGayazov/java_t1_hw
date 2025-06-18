package com.example.task1.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import com.example.task1.dto.ProcessedTransactionDto;
import com.example.task1.dto.TransactionDto;
import com.example.task1.entity.Account;
import com.example.task1.entity.Client;
import com.example.task1.entity.Transaction;
import com.example.task1.entity.enums.AccountStatus;
import com.example.task1.entity.enums.ClientStatus;
import com.example.task1.entity.enums.Metrics;
import com.example.task1.entity.enums.TransactionStatus;
import com.example.task1.mapper.TransactionMapper;
import com.example.task1.repository.AccountRepository;
import com.example.task1.repository.ClientRepository;
import com.example.task1.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private ClientServiceHttpClient httpClientService;

    @Mock
    private MetricServiceImpl metricService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private final Long TEST_ACCOUNT_ID = 1L;
    private final Long TEST_CLIENT_ID = 1L;
    private final Long TEST_TRANSACTION_ID = 1L;
    private final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(100.0);

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        List<Transaction> transactions = List.of(transaction1, transaction2);

        TransactionDto dto1 = new TransactionDto(1L, TEST_AMOUNT, TEST_ACCOUNT_ID, LocalDateTime.now(), "REQUESTED", 1L);
        TransactionDto dto2 = new TransactionDto(2L, TEST_AMOUNT, 2L, LocalDateTime.now(), "REJECTED", 2L);

        when(transactionRepository.findAll()).thenReturn(transactions);
        when(transactionMapper.toDto(transaction1)).thenReturn(dto1);
        when(transactionMapper.toDto(transaction2)).thenReturn(dto2);

        List<TransactionDto> result = transactionService.getAllTransactions();

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(transactionRepository).findAll();
    }

    @Test
    void getTransactionById_WhenExists_ShouldReturnTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(TEST_TRANSACTION_ID);
        TransactionDto expectedDto = new TransactionDto(TEST_TRANSACTION_ID, TEST_AMOUNT, TEST_ACCOUNT_ID, null, null, null);

        when(transactionRepository.findById(TEST_TRANSACTION_ID)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(expectedDto);

        TransactionDto result = transactionService.getTransactionById(TEST_TRANSACTION_ID);

        assertEquals(expectedDto, result);
        verify(transactionRepository).findById(TEST_TRANSACTION_ID);
    }

    @Test
    void getTransactionById_WhenNotExists_ShouldThrowException() {
        when(transactionRepository.findById(TEST_TRANSACTION_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getTransactionById(TEST_TRANSACTION_ID));
    }

    @Test
    void createTransaction_WhenTooManyRejected_ShouldArrestAccount() {
        transactionService.setMaxRejectedCount(3);

        Account account = new Account();
        account.setId(TEST_ACCOUNT_ID);
        account.setAccountStatus(AccountStatus.OPEN);

        Client client = new Client();
        client.setId(TEST_CLIENT_ID);
        client.setClientStatus(ClientStatus.ACTIVE);
        account.setClient(client);

        TransactionDto inputDto = new TransactionDto(null, TEST_AMOUNT, TEST_ACCOUNT_ID, null, null, null);
        Transaction transaction = new Transaction();

        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(httpClientService.getClientStatus(TEST_ACCOUNT_ID, TEST_CLIENT_ID)).thenReturn(ClientStatus.ACTIVE);
        when(transactionMapper.toEntity(inputDto)).thenReturn(transaction);
        when(transactionRepository.countByAccountAndStatus(account, TransactionStatus.REJECTED)).thenReturn(3L);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        ProcessedTransactionDto result = transactionService.createTransaction(inputDto);

        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());
        assertEquals(AccountStatus.ARRESTED, account.getAccountStatus());
        verify(metricService).increment(Metrics.ARRESTED_ACCOUNT_COUNT);
    }

    @Test
    void updateTransaction_ShouldUpdateFields() {
        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(TEST_TRANSACTION_ID);

        Account newAccount = new Account();
        newAccount.setId(2L);

        TransactionDto updateDto = new TransactionDto(TEST_TRANSACTION_ID, TEST_AMOUNT, 2L, null, null, null);
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAccount(newAccount);
        updatedTransaction.setAmount(TEST_AMOUNT);

        when(transactionRepository.findById(TEST_TRANSACTION_ID)).thenReturn(Optional.of(existingTransaction));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(newAccount));
        when(transactionRepository.save(existingTransaction)).thenReturn(updatedTransaction);
        when(transactionMapper.toDto(updatedTransaction)).thenReturn(updateDto);

        TransactionDto result = transactionService.updateTransactionById(TEST_TRANSACTION_ID, updateDto);

        assertEquals(updateDto, result);
        verify(transactionRepository).save(existingTransaction);
    }

    @Test
    void deleteTransaction_WhenExists_ShouldDelete() {
        when(transactionRepository.existsById(TEST_TRANSACTION_ID)).thenReturn(true);

        transactionService.deleteTransactionById(TEST_TRANSACTION_ID);

        verify(transactionRepository).deleteById(TEST_TRANSACTION_ID);
    }

    @Test
    void deleteTransaction_WhenNotExists_ShouldThrowException() {
        when(transactionRepository.existsById(TEST_TRANSACTION_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.deleteTransactionById(TEST_TRANSACTION_ID));
    }
}