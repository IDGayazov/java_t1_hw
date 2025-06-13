package com.example.starter.dto;


public record ErrorLogDto (
        String methodName,
        Exception exception,
        Long time
){
}
