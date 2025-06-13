package com.example.task1.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignUpRequest {
    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String middleName;

    private Set<String> role;

    private String password;
}
