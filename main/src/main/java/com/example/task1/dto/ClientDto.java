package com.example.task1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientDto(
        Long id,
        String firstName,
        String lastName,
        String middleName
){}
