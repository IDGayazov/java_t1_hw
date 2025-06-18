package com.example.task1.service.impl;

import com.example.task1.dto.UnblockRequestDto;
import com.example.task1.dto.UnblockResponseDto;
import com.example.task1.exception.UndefinedClientStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnblockServiceHttpClient {
    @Value("${unblockservice.url}")
    private String unblockServiceUrl;

    private final RestTemplate restTemplate;

    public List<UnblockResponseDto> unblockClients(List<UnblockRequestDto> request) {
        log.info("Request for unblock clients from unblockService");
        return unblock(request, "/clients");
    }

    public List<UnblockResponseDto> unblockAccounts(List<UnblockRequestDto> request) {
        log.info("Request for unblock accounts from unblockService");
        return unblock(request, "/accounts");
    }

    private List<UnblockResponseDto> unblock(List<UnblockRequestDto> request, String endpoint) {

        try {
            ResponseEntity<List<UnblockResponseDto>> response = restTemplate.exchange(
                    unblockServiceUrl + endpoint,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<>() {}
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new UndefinedClientStatusException("Invalid response from unblock service");
            }

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Unblock service error for endpoint {}: {}", endpoint, e.getResponseBodyAsString());
            throw new RestClientException("Error calling unblock service: " + e.getMessage());
        } catch (RestClientException e) {
            log.error("Rest client error for endpoint {}: {}", endpoint, e.getMessage());
            throw new RestClientException("Communication error with unblock service");
        }
    }

    public String getUnblockServiceUrl() {
        return unblockServiceUrl;
    }

    public void setUnblockServiceUrl(String unblockServiceUrl) {
        this.unblockServiceUrl = unblockServiceUrl;
    }
}
