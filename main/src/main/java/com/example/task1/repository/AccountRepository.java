package com.example.task1.repository;

import com.example.task1.annotation.Cached;
import com.example.task1.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Cached
    Optional<Account> findById(Long id);

    @Cached
    List<Account> findAll();

}
