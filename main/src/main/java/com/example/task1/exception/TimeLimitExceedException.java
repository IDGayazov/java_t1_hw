package com.example.task1.exception;

public class TimeLimitExceedException extends RuntimeException {
    public TimeLimitExceedException(String message) {
        super(message);
    }
}
