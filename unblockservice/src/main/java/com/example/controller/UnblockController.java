package com.example.controller;

import com.example.dto.RequestDto;
import com.example.dto.ResponseDto;
import com.example.service.UnblockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/unblock")
@RequiredArgsConstructor
public class UnblockController {

    private final UnblockService unblockService;

    @PostMapping("/clients")
    public List<ResponseDto> unblockClients(@RequestBody List<RequestDto> request){
        log.info("Request for unblock clients: {}", request);
        return unblockService.unblock(request);
    }

    @PostMapping("/accounts")
    public List<ResponseDto> unblockAccounts(@RequestBody List<RequestDto> request){
        log.info("Request for unblock accounts: {}", request);
        return unblockService.unblock(request);
    }

}
