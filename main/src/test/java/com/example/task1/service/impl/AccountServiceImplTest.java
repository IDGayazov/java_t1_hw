package com.example.task1.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import com.example.task1.dto.AccountDto;
import com.example.task1.entity.Account;
import com.example.task1.entity.Client;
import com.example.task1.entity.enums.AccountType;
import com.example.task1.mapper.AccountMapper;
import com.example.task1.repository.AccountRepository;
import com.example.task1.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        Account account1 = new Account();
        Account account2 = new Account();
        List<Account> accounts = List.of(account1, account2);

        AccountDto accountDto1 = new AccountDto(1L, BigDecimal.valueOf(100.0), "DEBIT", 1L, "OPEN", BigDecimal.valueOf(100.0), 1l);
        AccountDto accountDto2 = new AccountDto(2L, BigDecimal.valueOf(100.0), "DEBIT", 1L, "OPEN", BigDecimal.valueOf(100.0), 1l);

        when(accountRepository.findAll()).thenReturn(accounts);
        when(accountMapper.toDto(account1)).thenReturn(accountDto1);
        when(accountMapper.toDto(account2)).thenReturn(accountDto2);

        List<AccountDto> result = accountService.getAllAccounts();

        assertEquals(2, result.size());
        assertEquals(accountDto1, result.get(0));
        assertEquals(accountDto2, result.get(1));
        verify(accountRepository).findAll();
    }

    @Test
    void getAccountById_WhenAccountExists_ShouldReturnAccount() {
        Long accountId = 1L;
        Account account = new Account();
        AccountDto expectedDto = new AccountDto(1L, BigDecimal.valueOf(100.0), "DEBIT", 1L, "OPEN", BigDecimal.valueOf(100.0), 1l);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(expectedDto);

        AccountDto result = accountService.getAccountById(accountId);

        assertEquals(expectedDto, result);
        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccountById_WhenAccountNotExists_ShouldThrowException() {

        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccountById(accountId));
        verify(accountRepository).findById(accountId);
    }

    @Test
    void createAccount_WhenClientExists_ShouldCreateAccount() {
        Long clientId = 1L;
        AccountDto inputDto = new AccountDto( null, BigDecimal.valueOf(100.0), "DEBIT", 1L, "OPEN", BigDecimal.valueOf(100.0), 1l);
        Client client = new Client();
        Account account = new Account();
        Account savedAccount = new Account();
        savedAccount.setId(1L);
        AccountDto expectedDto = new AccountDto(1L, BigDecimal.valueOf(100.0), "DEBIT", 1L, "OPEN", BigDecimal.valueOf(100.0), 1l);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountMapper.toEntity(inputDto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(savedAccount);
        when(accountMapper.toDto(savedAccount)).thenReturn(expectedDto);

        AccountDto result = accountService.createAccount(inputDto);

        assertEquals(expectedDto, result);
        verify(clientRepository).findById(clientId);
        verify(accountRepository).save(account);
    }

    @Test
    void createAccount_WhenClientNotExists_ShouldThrowException() {
        Long clientId = 1L;
        AccountDto inputDto = new AccountDto(1L, BigDecimal.valueOf(100.0), "DEBIT", 1L, "OPEN", BigDecimal.valueOf(100.0), 1l);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.createAccount(inputDto));
        verify(clientRepository).findById(clientId);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccountById_WhenAccountExists_ShouldUpdateAccount() {
        Long accountId = 1L;
        Long clientId = 1L;
        AccountDto updateDto = new AccountDto(
                accountId,
                BigDecimal.valueOf(100.0),
                "DEBIT",
                clientId,
                "OPEN",
                BigDecimal.valueOf(100.0),
                1L
        );

        Account existingAccount = new Account();
        existingAccount.setId(accountId);

        Client client = new Client();
        client.setId(clientId);

        Account updatedAccount = new Account();
        updatedAccount.setId(accountId);

        AccountDto expectedDto = new AccountDto(
                accountId,
                BigDecimal.valueOf(100.0),
                "DEBIT",
                clientId,
                "OPEN",
                BigDecimal.valueOf(100.0),
                1L
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.save(existingAccount)).thenReturn(updatedAccount);
        when(accountMapper.toDto(updatedAccount)).thenReturn(expectedDto);

        AccountDto result = accountService.updateAccountById(accountId, updateDto);

        assertEquals(expectedDto, result);
        verify(accountRepository).findById(accountId);
        verify(clientRepository).findById(clientId);
        verify(accountRepository).save(existingAccount);
    }

    @Test
    void updateAccountById_WhenAccountNotExists_ShouldThrowException() {
        Long accountId = 1L;
        AccountDto updateDto = new AccountDto(1L, BigDecimal.valueOf(100.0), "DEBIT", 1L, "OPEN", BigDecimal.valueOf(100.0), 1l);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                accountService.updateAccountById(accountId, updateDto));
        verify(accountRepository).findById(accountId);
        verify(clientRepository, never()).findById(anyLong());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccountById_WhenClientNotExists_ShouldThrowException() {
        Long accountId = 1L;
        Long clientId = 2L;
        AccountDto updateDto = new AccountDto(accountId, BigDecimal.valueOf(100.0), "DEBIT", clientId, "OPEN", BigDecimal.valueOf(100.0), 1L);

        Account existingAccount = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                accountService.updateAccountById(accountId, updateDto));
        verify(accountRepository).findById(accountId);
        verify(clientRepository).findById(clientId);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccountById_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        Long accountId = 1L;
        Long clientId = 1L;

        Account existingAccount = new Account();
        existingAccount.setId(accountId);
        existingAccount.setBalance(BigDecimal.valueOf(100.0));
        existingAccount.setAccountType(AccountType.CREDIT);
        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingAccount.setClient(existingClient);

        Account updatedAccount = new Account();
        updatedAccount.setId(accountId);
        updatedAccount.setBalance(BigDecimal.valueOf(100.0));
        updatedAccount.setAccountType(AccountType.DEBIT);
        updatedAccount.setClient(existingClient);

        AccountDto updateDto = new AccountDto(
                accountId,
                null,
                "DEBIT",
                null,
                null,
                null,
                null
        );

        AccountDto expectedDto = new AccountDto(
                accountId,
                BigDecimal.valueOf(100.0),
                "DEBIT",
                clientId,
                null,
                null,
                null
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(updatedAccount);
        when(accountMapper.toDto(updatedAccount)).thenReturn(expectedDto);

        AccountDto result = accountService.updateAccountById(accountId, updateDto);

        assertEquals(expectedDto, result);
        assertEquals(AccountType.DEBIT, existingAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(100.0), existingAccount.getBalance());
        assertSame(existingClient, existingAccount.getClient());
        verify(accountRepository).findById(accountId);
        verify(clientRepository, never()).findById(anyLong());
        verify(accountRepository).save(existingAccount);
        verify(accountMapper).toDto(updatedAccount);
    }

    @Test
    void deleteAccountById_WhenAccountExists_ShouldDeleteAccount() {
        Long accountId = 1L;
        when(accountRepository.existsById(accountId)).thenReturn(true);

        accountService.deleteAccountById(accountId);

        verify(accountRepository).existsById(accountId);
        verify(accountRepository).deleteById(accountId);
    }

    @Test
    void deleteAccountById_WhenAccountNotExists_ShouldThrowException() {
        Long accountId = 1L;
        when(accountRepository.existsById(accountId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                accountService.deleteAccountById(accountId));
        verify(accountRepository).existsById(accountId);
        verify(accountRepository, never()).deleteById(anyLong());
    }
}