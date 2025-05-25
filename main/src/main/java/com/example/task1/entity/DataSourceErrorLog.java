package com.example.task1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="data_source_error_log")
public class DataSourceErrorLog {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "data_error_log_sequence")
    @SequenceGenerator(name="data_error_log_sequence", sequenceName="data_error_log_seq")
    private Long id;

    @Column(name="stacktrace_text")
    private String stacktraceText;

    @Column(name="message")
    private String message;

    @Column(name="method_signature")
    private String methodSignature;
}
