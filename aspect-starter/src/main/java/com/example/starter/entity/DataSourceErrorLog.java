package com.example.starter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceErrorLog {
    private Long id;
    private String stacktraceText;
    private String message;
    private String methodSignature;
}
