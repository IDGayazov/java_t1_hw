package com.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class CheckClientService {

    public String getClientStatus(){
        boolean isBlocked = new Random().nextInt(100) < 5;
        log.info("Client status: {}", isBlocked ? "BLOCKED" : "ACTIVE");
        return isBlocked ? "BLOCKED" : "ACTIVE";
    }

}
