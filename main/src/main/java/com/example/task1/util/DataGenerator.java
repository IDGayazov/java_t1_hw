package com.example.task1.util;

import com.example.task1.entity.Account;
import com.example.task1.entity.Client;
import com.example.task1.entity.Transaction;
import com.example.task1.entity.User;
import com.example.task1.entity.enums.AccountStatus;
import com.example.task1.entity.enums.AccountType;
import com.example.task1.entity.enums.TransactionStatus;
import com.example.task1.mapper.AccountMapper;
import com.example.task1.mapper.ClientMapper;
import com.example.task1.mapper.TransactionMapper;
import com.example.task1.repository.AccountRepository;
import com.example.task1.repository.ClientRepository;
import com.example.task1.repository.TransactionRepository;
import com.example.task1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Profile("dev")
@ConditionalOnProperty(
        name="app.data.generator.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
@Component
public class DataGenerator implements CommandLineRunner {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    private final ClientMapper clientMapper;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;

    private List<Client> clients = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();

    private void saveClients(){
        User user1 = User.builder()
                .login("ivan")
                .email("ivan@mail.com")
                .password("$2a$10$YlAA8IGBoyMOayVHH.QIyeqsoz8uOub8lJUr90bUKsk7NszxQF2NK")
                .build();

        User user2 = User.builder()
                .login("igor")
                .email("igor@mail.com")
                .password("$2a$10$YlAA8IGBoyMOayVHH.QIyeqsoz8uOub8lJUr90bUKsk7NszxQF2NK")
                .build();

        User user3 = User.builder()
                .login("andrey")
                .email("andrey@mail.com")
                .password("$2a$10$YlAA8IGBoyMOayVHH.QIyeqsoz8uOub8lJUr90bUKsk7NszxQF2NK")
                .build();

        User user4 = User.builder()
                .login("aleksey")
                .email("aleksey@mail.com")
                .password("$2a$10$YlAA8IGBoyMOayVHH.QIyeqsoz8uOub8lJUr90bUKsk7NszxQF2NK")
                .build();

        User user5 = User.builder()
                .login("matvey")
                .email("matvey@mail.co")
                .password("$2a$10$YlAA8IGBoyMOayVHH.QIyeqsoz8uOub8lJUr90bUKsk7NszxQF2NK")
                .build();

        Client client1 = Client.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .clientId(234L)
                .user(user1)
                .build();

        Client client2 = Client.builder()
                .firstName("Igor")
                .lastName("Sergeev")
                .middleName("Semenovich")
                .clientId(189L)
                .user(user2)
                .build();

        Client client3 = Client.builder()
                .firstName("Andrey")
                .lastName("Dmitriev")
                .middleName("Sergeevich")
                .clientId(178L)
                .user(user3)
                .build();

        Client client4 = Client.builder()
                .firstName("Aleksey")
                .lastName("Gomonov")
                .middleName("Viktotovich")
                .clientId(153L)
                .user(user4)
                .build();

        Client client5 = Client.builder()
                .firstName("Matvey")
                .lastName("Maksimov")
                .middleName("Ivanovich")
                .clientId(156L)
                .user(user5)
                .build();

        userRepository.saveAll(List.of(user1, user2, user3, user4, user5));
        clientRepository.saveAll(List.of(client1, client2, client3, client4, client5));

        clients.addAll(List.of(client2, client3, client5));
    }

    private void saveAccounts(){
        Account account1 = Account.builder()
                .balance(BigDecimal.valueOf(1234.3))
                .accountType(AccountType.valueOf("DEBIT"))
                .client(clients.get(0))
                .accountId(167L)
                .accountStatus(AccountStatus.OPEN)
                .frozenAmount(BigDecimal.valueOf(0.0))
                .build();

        Account account2 = Account.builder()
                .balance(BigDecimal.valueOf(1234.34))
                .accountType(AccountType.valueOf("CREDIT"))
                .client(clients.get(1))
                .accountId(137L)
                .accountStatus(AccountStatus.OPEN)
                .frozenAmount(BigDecimal.valueOf(0.0))
                .build();

        Account account3 = Account.builder()
                .balance(BigDecimal.valueOf(345.34))
                .accountType(AccountType.valueOf("DEBIT"))
                .client(clients.get(2))
                .accountId(169L)
                .frozenAmount(BigDecimal.valueOf(0.0))
                .accountStatus(AccountStatus.OPEN)
                .build();

        accountRepository.saveAll(List.of(account1, account2, account3));
        accounts.addAll(List.of(account1, account2, account3));
    }

    private void saveTransactions(){
        Transaction transaction1 = Transaction.builder()
                .account(accounts.get(0))
                .amount(BigDecimal.valueOf(123.4))
                .time(LocalDateTime.now())
                .transactionId(126L)
                .status(TransactionStatus.ACCEPTED)
                .build();

        Transaction transaction2 = Transaction.builder()
                .account(accounts.get(1))
                .amount(BigDecimal.valueOf(345.7))
                .time(LocalDateTime.now())
                .transactionId(123L)
                .status(TransactionStatus.ACCEPTED)
                .build();

        Transaction transaction3 = Transaction.builder()
                .account(accounts.get(2))
                .amount(BigDecimal.valueOf(1234.3))
                .time(LocalDateTime.now())
                .transactionId(232L)
                .status(TransactionStatus.ACCEPTED)
                .build();

        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3));
    }

    @Override
    public void run(String... args) throws Exception {
        saveClients();
        saveAccounts();
        saveTransactions();
    }
}
