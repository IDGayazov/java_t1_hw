package com.example.task1.repository;

import com.example.starter.annotation.Cached;
import com.example.task1.entity.Account;
import com.example.task1.entity.Transaction;
import com.example.task1.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Cached
    Optional<Transaction> findById(Long id);

    @Cached
    List<Transaction> findAll();

    long countByAccountAndStatus(Account account, TransactionStatus status);

}
