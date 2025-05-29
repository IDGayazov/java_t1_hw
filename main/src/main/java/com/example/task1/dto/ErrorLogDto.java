package com.example.task1.dto;

import org.aspectj.lang.reflect.MethodSignature;

public record ErrorLogDto(
        MethodSignature methodSignature,
        Exception exception,
        Long time
) {
}
