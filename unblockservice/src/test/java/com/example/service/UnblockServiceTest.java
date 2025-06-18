package com.example.service;

import com.example.dto.RequestDto;
import com.example.dto.ResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnblockServiceTest {

    @InjectMocks
    private UnblockService unblockService;

    @Test
    void unblock_shouldReturnResponseForEachRequest() {
        List<RequestDto> requests = List.of(
                new RequestDto(1L),
                new RequestDto(2L),
                new RequestDto(3L)
        );

        List<ResponseDto> responses = unblockService.unblock(requests);

        assertEquals(requests.size(), responses.size());
        for (int i = 0; i < requests.size(); i++) {
            assertEquals(requests.get(i).id(), responses.get(i).id());
        }
    }

    @Test
    void unblock_shouldHandleEmptyList() {
        List<RequestDto> emptyList = List.of();

        List<ResponseDto> responses = unblockService.unblock(emptyList);

        assertTrue(responses.isEmpty());
    }
}
