package com.example.task1.service.impl;

import com.example.task1.dto.UnblockRequestDto;
import com.example.task1.dto.UnblockResponseDto;
import com.example.task1.exception.UndefinedClientStatusException;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UnblockServiceHttpClientIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private UnblockServiceHttpClient createClient() {
        UnblockServiceHttpClient client = new UnblockServiceHttpClient(restTemplateBuilder.build());
        client.setUnblockServiceUrl("http://localhost:" + wireMockServer.getPort());
        return client;
    }

    @Test
    void unblockClients_ShouldReturnSuccessResponse() {
        UnblockServiceHttpClient client = createClient();
        List<UnblockRequestDto> request = List.of(new UnblockRequestDto(1L));
        List<UnblockResponseDto> expectedResponse = List.of(new UnblockResponseDto(1L, true));

        wireMockServer.stubFor(post(urlEqualTo("/clients"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[{\"id\":1,\"isBlocked\":true}]")));

        List<UnblockResponseDto> response = client.unblockClients(request);

        assertEquals(expectedResponse, response);
        wireMockServer.verify(postRequestedFor(urlEqualTo("/clients")));
    }

    @Test
    void unblockAccounts_ShouldReturnSuccessResponse() {
        UnblockServiceHttpClient client = createClient();
        List<UnblockRequestDto> request = List.of(new UnblockRequestDto(1L));
        List<UnblockResponseDto> expectedResponse = List.of(new UnblockResponseDto(1L, true));

        wireMockServer.stubFor(post(urlEqualTo("/accounts"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[{\"id\":1,\"isBlocked\":true}]")));

        List<UnblockResponseDto> response = client.unblockAccounts(request);

        assertEquals(expectedResponse, response);
        wireMockServer.verify(postRequestedFor(urlEqualTo("/accounts")));
    }

    @Test
    void unblockClients_WhenServiceReturnsError_ShouldThrowException() {
        UnblockServiceHttpClient client = createClient();
        List<UnblockRequestDto> request = List.of(new UnblockRequestDto(1L));

        wireMockServer.stubFor(post(urlEqualTo("/clients"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withBody("Error message")));

        assertThrows(RestClientException.class, () -> client.unblockClients(request));
    }

    @Test
    void unblockClients_WhenServiceReturnsInvalidResponse_ShouldThrowException() {
        UnblockServiceHttpClient client = createClient();
        List<UnblockRequestDto> request = List.of(new UnblockRequestDto(1L));

        wireMockServer.stubFor(post(urlEqualTo("/clients"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody("invalid json")));

        assertThrows(RestClientException.class, () -> client.unblockClients(request));
    }

    @Test
    void unblockClients_WhenServiceReturnsEmptyBody_ShouldThrowException() {
        UnblockServiceHttpClient client = createClient();
        List<UnblockRequestDto> request = List.of(new UnblockRequestDto(1L));

        wireMockServer.stubFor(post(urlEqualTo("/clients"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

        assertThrows(UndefinedClientStatusException.class, () -> client.unblockClients(request));
    }

    @Test
    void unblockClients_WhenServiceUnavailable_ShouldThrowException() {
        UnblockServiceHttpClient client = createClient();
        List<UnblockRequestDto> request = List.of(new UnblockRequestDto(1L));

        wireMockServer.stubFor(post(urlEqualTo("/clients"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));

        assertThrows(RestClientException.class, () -> client.unblockClients(request));
    }
}
