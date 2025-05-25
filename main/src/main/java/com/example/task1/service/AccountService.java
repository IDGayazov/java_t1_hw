package com.example.task1.service;

import com.example.task1.dto.AccountDto;
import com.example.task1.entity.Account;

public interface AccountService {
    AccountDto getAccountById(Long id);
    AccountDto createAccount(AccountDto accountDto);
    AccountDto updateAccountById(Long id, AccountDto accountDto);
    void deleteAccountById(Long id);
}
