package com.example.task1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.aspectj.lang.reflect.MethodSignature;

@Data
@AllArgsConstructor
public class ErrorLogDto{
        private String methodName;
        private Exception exception;
        private Long time;
}
