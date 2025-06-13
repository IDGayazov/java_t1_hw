package com.example.task1.repository;

import com.example.starter.annotation.Cached;
import com.example.task1.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Cached
    Optional<Client> findById(Long id);
}
