package com.example.task1.repository;

import com.example.starter.annotation.Cached;
import com.example.task1.entity.Account;
import com.example.task1.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Cached
    Optional<Account> findById(Long id);

    @Cached
    List<Account> findAll();

    @Query(value = "SELECT * FROM account WHERE account_status = :status ORDER BY id LIMIT :limit",
            nativeQuery = true)
    List<Account> findAccountsForUnblockingNative(
            @Param("status") String status,
            @Param("limit") int limit
    );

}
