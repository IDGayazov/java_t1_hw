package com.example.task1.repository;

import com.example.starter.annotation.Cached;
import com.example.task1.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Cached
    Optional<Client> findById(Long id);

    @Query(value = "SELECT * FROM client WHERE status = :status ORDER BY id LIMIT :limit",
            nativeQuery = true)
    List<Client> findClientsForUnblockingNative(
            @Param("status") String status,
            @Param("limit") int limit
    );
}
