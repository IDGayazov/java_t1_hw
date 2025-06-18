package com.example.task1.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.task1.entity.enums.ClientStatus;
import com.example.task1.exception.UndefinedClientStatusException;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.example.task1.entity.enums.ClientStatus.ACTIVE;
import static com.example.task1.entity.enums.ClientStatus.BLOCKED;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientServiceHttpClientTest {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private ClientServiceHttpClient clientServiceHttpClient;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        clientServiceHttpClient = new ClientServiceHttpClient(restTemplateBuilder.build());
        clientServiceHttpClient.setClientServiceUrl("http://localhost:" + wireMockServer.getPort());
    }

    @Test
    void getClientStatus_ShouldReturnActiveStatus() {
        Long accountId = 1L;
        Long clientId = 1L;
        String expectedStatus = "ACTIVE";

        wireMockServer.stubFor(post(urlEqualTo("/checkClientStatus"))
                .withHeader("Authorization", equalTo("Bearer test-token"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"status\":\"" + expectedStatus + "\"}")));

        ClientStatus result = clientServiceHttpClient.getClientStatus(accountId, clientId);

        assertEquals(ACTIVE, result);
        wireMockServer.verify(postRequestedFor(urlEqualTo("/checkClientStatus"))
                .withRequestBody(equalToJson("{\"accountId\":1,\"clientId\":1}")));
    }

    @Test
    void getClientStatus_ShouldReturnBlockedStatus() {
        String expectedStatus = "BLOCKED";

        wireMockServer.stubFor(post(urlEqualTo("/checkClientStatus"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"status\":\"" + expectedStatus + "\"}")));

        ClientStatus result = clientServiceHttpClient.getClientStatus(1L, 1L);

        assertEquals(BLOCKED, result);
    }

    @Test
    void getClientStatus_WhenServiceReturnsError_ShouldThrowException() {
        wireMockServer.stubFor(post(urlEqualTo("/checkClientStatus"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withBody("Error message")));

        assertThrows(UndefinedClientStatusException.class,
                () -> clientServiceHttpClient.getClientStatus(1L, 1L));
    }

    @Test
    void getClientStatus_WhenInvalidAuthHeader_ShouldThrowSecurityException() {
        RequestContextHolder.resetRequestAttributes();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThrows(SecurityException.class,
                () -> clientServiceHttpClient.getClientStatus(1L, 1L));
    }

    @Test
    void getClientStatus_WhenEmptyResponse_ShouldThrowException() {
        wireMockServer.stubFor(post(urlEqualTo("/checkClientStatus"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

        assertThrows(UndefinedClientStatusException.class,
                () -> clientServiceHttpClient.getClientStatus(1L, 1L));
    }

    @Test
    void getClientStatus_WhenInvalidStatus_ShouldThrowException() {
        wireMockServer.stubFor(post(urlEqualTo("/checkClientStatus"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"status\":\"INVALID_STATUS\"}")));

        assertThrows(IllegalArgumentException.class,
                () -> clientServiceHttpClient.getClientStatus(1L, 1L));
    }
}