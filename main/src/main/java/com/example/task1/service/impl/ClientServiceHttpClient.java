package com.example.task1.service.impl;

import com.example.task1.dto.ClientStatusRequestDto;
import com.example.task1.dto.ClientStatusResponseDto;
import com.example.task1.entity.enums.ClientStatus;
import com.example.task1.exception.UndefinedClientStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceHttpClient {
    @Value("${clientservice.url}")
    private String clientServiceUrl;

    private final RestTemplate restTemplate;

    public ClientStatus getClientStatus(Long accountId, Long clientId) {
        log.info("Request for clientService");

        String authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest()
                .getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String jwt = authHeader;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ClientStatusRequestDto requestDto = new ClientStatusRequestDto(accountId, clientId);

        try {
            ResponseEntity<ClientStatusResponseDto> response = restTemplate.exchange(
                    clientServiceUrl + "/checkClientStatus",
                    HttpMethod.POST,
                    new HttpEntity<>(requestDto, headers),
                    ClientStatusResponseDto.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new UndefinedClientStatusException("Invalid response from client service");
            }

            return ClientStatus.valueOf(response.getBody().status());
        } catch (HttpClientErrorException e) {
            log.error("Client service error: {}", e.getResponseBodyAsString());
            throw new UndefinedClientStatusException("Error calling client service: " + e.getMessage());
        }
    }

    public String getClientServiceUrl() {
        return clientServiceUrl;
    }

    public void setClientServiceUrl(String clientServiceUrl) {
        this.clientServiceUrl = clientServiceUrl;
    }
}
