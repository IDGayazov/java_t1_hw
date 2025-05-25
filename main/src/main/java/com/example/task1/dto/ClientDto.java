package com.example.task1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientDto(
        Long id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        @JsonProperty("middle_name")
        String middleName
){}
