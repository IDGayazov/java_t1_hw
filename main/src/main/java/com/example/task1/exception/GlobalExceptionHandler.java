package com.example.task1.exception;

import com.example.task1.dto.ResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDto> entityNotFoundExceptionHandler(EntityNotFoundException ex){
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new ResponseDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalError.class)
    public ResponseEntity<ResponseDto> internalServerErrorHandler(InternalError error){
        log.error(error.getMessage(), error);
        return new ResponseEntity<>(new ResponseDto(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
