package com.example.task1.repository;

import com.example.task1.entity.Role;
import com.example.task1.entity.User;
import com.example.task1.entity.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum role);
}
