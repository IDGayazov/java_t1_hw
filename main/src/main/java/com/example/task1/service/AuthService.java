package com.example.task1.service;

import com.example.task1.dto.JwtResponse;
import com.example.task1.dto.LoginRequest;
import com.example.task1.dto.SignUpRequest;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    void register(SignUpRequest request);
}
