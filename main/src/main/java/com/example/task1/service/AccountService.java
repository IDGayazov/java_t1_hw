package com.example.task1.service;

import com.example.task1.dto.AccountDto;
import com.example.task1.entity.Account;

import java.util.List;

public interface AccountService {
    List<AccountDto> getAllAccounts();
    AccountDto getAccountById(Long id);
    AccountDto createAccount(AccountDto accountDto);
    AccountDto updateAccountById(Long id, AccountDto accountDto);
    void deleteAccountById(Long id);
}
