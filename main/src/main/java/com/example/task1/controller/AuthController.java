package com.example.task1.controller;

import com.example.task1.dto.JwtResponse;
import com.example.task1.dto.LoginRequest;
import com.example.task1.dto.ResponseDto;
import com.example.task1.dto.SignUpRequest;
import com.example.task1.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseDto registerUser(@RequestBody SignUpRequest signUpRequest) {
        authService.register(signUpRequest);
        return new ResponseDto("User registered successfully!");
    }
}
