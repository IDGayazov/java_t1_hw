package com.example.controller;

import com.example.dto.ClientCheckRequest;
import com.example.dto.ClientCheckResponse;
import com.example.service.CheckClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ClientCheckController {

    private final CheckClientService clientService;

    @PostMapping("/checkClientStatus")
    public ClientCheckResponse checkClient(
            @RequestBody ClientCheckRequest request
    ) {
        log.info("Received request for check client status: {}", request);
        return new ClientCheckResponse(clientService.getClientStatus());
    }
}
