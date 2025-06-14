package com.example.task1.service.impl;

import com.example.task1.dto.UnblockRequestDto;
import com.example.task1.dto.UnblockResponseDto;
import com.example.task1.entity.Account;
import com.example.task1.entity.Client;
import com.example.task1.entity.enums.AccountStatus;
import com.example.task1.entity.enums.ClientStatus;
import com.example.task1.repository.AccountRepository;
import com.example.task1.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnblockService {

    @Value("${unblockservice.client-count}")
    private int unblockClientCount = 3;

    @Value("${unblockservice.account-count}")
    private int unblockAccountCount = 3;

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final UnblockServiceHttpClient httpClient;

    @Scheduled(fixedRateString = "${unblockservice.interval}")
    public void unblockClients(){
        log.info("Unblock blocked clients");
        List<Client> blockedClients = clientRepository.findClientsForUnblockingNative(
                ClientStatus.BLOCKED.name(),
                unblockClientCount
        );

        Map<Long, Client> clientsMap = blockedClients.stream()
                .collect(Collectors.toMap(Client::getId, Function.identity()));

        List<UnblockRequestDto> requests = blockedClients.stream()
                .map(client -> new UnblockRequestDto(client.getId()))
                .toList();

        httpClient.unblockClients(requests).stream()
                .filter(response -> !response.isBlocked())
                .map(UnblockResponseDto::id)
                .map(clientsMap::get)
                .filter(Objects::nonNull)
                .forEach(this::unblockClient);

    }

    @Scheduled(fixedRateString = "${unblockservice.interval}")
    public void unblockAccounts(){
        log.info("Unblock arrested accounts");

        List<Account> blockedAccounts = accountRepository.findAccountsForUnblockingNative(
                AccountStatus.ARRESTED.name(),
                unblockAccountCount
        );

        Map<Long, Account> accountsMap = blockedAccounts.stream()
                .collect(Collectors.toMap(Account::getId, Function.identity()));

        List<UnblockRequestDto> requests = blockedAccounts.stream()
                .map(account -> new UnblockRequestDto(account.getId()))
                .toList();

        httpClient.unblockAccounts(requests).stream()
                .filter(response -> !response.isBlocked())
                .map(UnblockResponseDto::id)
                .map(accountsMap::get)
                .filter(Objects::nonNull)
                .forEach(this::unblockAccount);
    }

    private void unblockClient(Client client){
        client.setClientStatus(ClientStatus.ACTIVE);
        clientRepository.save(client);
    }

    private void unblockAccount(Account account){
        account.setAccountStatus(AccountStatus.OPEN);
        accountRepository.save(account);
    }

}
