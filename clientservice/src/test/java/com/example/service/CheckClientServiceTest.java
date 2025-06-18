package com.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckClientServiceTest {

    @Mock
    private Random random;

    @InjectMocks
    private CheckClientService checkClientService;

    @Test
    void getClientStatus_shouldReturnBlockedWhenRandomBelow5() {
        when(random.nextInt(anyInt())).thenReturn(4);
        String result = checkClientService.getClientStatus();
        assertEquals("BLOCKED", result);
    }

    @Test
    void getClientStatus_shouldReturnActiveWhenRandom5OrAbove() {
        when(random.nextInt(anyInt())).thenReturn(5);
        String result = checkClientService.getClientStatus();
        assertEquals("ACTIVE", result);
    }
}