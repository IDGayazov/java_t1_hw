package com.example.task1.controller;

import com.example.task1.dto.AccountDto;
import com.example.task1.entity.Account;
import com.example.task1.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable("id") Long id) {
        log.debug("Getting account with id: {}", id);
        return accountService.getAccountById(id);
    }

    @PostMapping
    public Account create(@RequestBody AccountDto accountDto) {
        log.info("Registering client: {}", accountDto);
        Account account = accountService.createAccount(accountDto);
        log.info("Client registered: {}", account.getId());
        return account;
    }

    @PutMapping("/{id}")
    public Account update(@PathVariable("id") long id, @RequestBody AccountDto accountDto) {
        log.info("Updating account with id: {}", id);
        return accountService.updateAccountById(id, accountDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        log.info("Deleting account with id: {}", id);
        accountService.deleteAccountById(id);
    }

}
