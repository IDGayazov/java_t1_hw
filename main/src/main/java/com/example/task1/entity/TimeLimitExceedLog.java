package com.example.task1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="time_limit_exceed_log")
public class TimeLimitExceedLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "time_limit_exceed_log_sequence")
    @SequenceGenerator(
            name="time_limit_exceed_log_sequence",
            sequenceName="time_limit_exceed_log_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name="exceed_time")
    private Long time;

    @Column(name="method_signature")
    private String methodSignature;
}
