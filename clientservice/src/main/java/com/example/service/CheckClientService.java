package com.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckClientService {

    private final Random random;

    public String getClientStatus(){
        boolean isBlocked = random.nextInt(100) < 5;
        log.info("Client status: {}", isBlocked ? "BLOCKED" : "ACTIVE");
        return isBlocked ? "BLOCKED" : "ACTIVE";
    }

}
