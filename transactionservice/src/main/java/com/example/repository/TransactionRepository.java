package com.example.repository;

import com.example.model.Account;
import com.example.model.Transaction;
import com.example.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    long countByAccountAndTimeAfter(Account account, LocalDateTime time);

    List<Transaction> findByAccountAndTimeAfterAndStatus(
            Account account,
            LocalDateTime startTime,
            TransactionStatus status
    );
}
