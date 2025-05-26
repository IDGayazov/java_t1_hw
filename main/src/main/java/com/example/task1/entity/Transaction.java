package com.example.task1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="financial_transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "transaction_sequence")
    @SequenceGenerator(name="transaction_sequence", sequenceName="transaction_seq")
    private Long id;

    @Column(name = "amount", precision = 13, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name="transaction_time")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
}
