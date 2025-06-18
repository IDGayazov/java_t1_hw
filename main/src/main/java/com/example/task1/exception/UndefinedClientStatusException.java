package com.example.task1.exception;

public class UndefinedClientStatusException extends RuntimeException {
    public UndefinedClientStatusException(String message) {
        super(message);
    }
}
