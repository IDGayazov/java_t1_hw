package com.example.task1.service.impl;

import com.example.starter.annotation.LoggingException;
import com.example.starter.annotation.Metric;
import com.example.task1.dto.ProcessedTransactionDto;
import com.example.task1.dto.TransactionDto;
import com.example.task1.entity.Account;
import com.example.task1.entity.Client;
import com.example.task1.entity.Transaction;
import com.example.task1.entity.enums.AccountStatus;
import com.example.task1.entity.enums.ClientStatus;
import com.example.task1.entity.enums.TransactionStatus;
import com.example.task1.mapper.TransactionMapper;
import com.example.task1.repository.AccountRepository;
import com.example.task1.repository.ClientRepository;
import com.example.task1.repository.TransactionRepository;
import com.example.task1.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${transaction.max-rejected}")
    private long maxRejectedCount;

    private final ClientServiceHttpClient httpClientService;

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final ClientRepository clientRepository;

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

        Long clientId = account.getClient().getId();
        boolean shouldReject = false;
        boolean shouldArrest = false;

        ClientStatus clientStatus = httpClientService.getClientStatus(transactionDto.accountId(), clientId);

        if (clientStatus == ClientStatus.BLOCKED) {
            blockClient(clientId);
            blockAccount(account);
            shouldReject = true;
        }

        if (!AccountStatus.OPEN.equals(account.getAccountStatus())) {
            throw new IllegalStateException("Account is not in OPEN status");
        }

        long rejectedCount = transactionRepository.countByAccountAndStatus(
                account,
                TransactionStatus.REJECTED
        );

        if (rejectedCount >= maxRejectedCount) {
            shouldReject = true;
            shouldArrest = true;
        }

        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setAccount(account);
        transaction.setTime(LocalDateTime.now());

        if (shouldReject) {
            transaction.setStatus(TransactionStatus.REJECTED);

            if (shouldArrest) {
                account.setAccountStatus(AccountStatus.ARRESTED);
                accountRepository.save(account);
                log.warn("Account {} arrested due to too many rejected transactions", account.getId());
            }
        } else {
            transaction.setStatus(TransactionStatus.REQUESTED);
            account.setBalance(account.getBalance().add(transactionDto.amount()));
            accountRepository.save(account);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new ProcessedTransactionDto(
                savedTransaction.getAccount().getClient().getClientId(),
                account.getAccountId(),
                savedTransaction.getId(),
                savedTransaction.getTime(),
                savedTransaction.getAmount(),
                savedTransaction.getAccount().getBalance()
        );
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

    private void blockClient(Long clientId){
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + clientId));

        client.setClientStatus(ClientStatus.BLOCKED);
        clientRepository.save(client);
    }

    private void blockAccount(Account account){
        account.setAccountStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
    }
}
