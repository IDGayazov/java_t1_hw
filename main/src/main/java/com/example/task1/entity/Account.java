package com.example.task1.entity;

import com.example.task1.entity.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="account")
public class Account {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "account_sequence")
    @SequenceGenerator(name="account_sequence", sequenceName="account_seq")
    private Long id;

    @Column(name = "balance", precision = 13, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
