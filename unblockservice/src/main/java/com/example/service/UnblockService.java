package com.example.service;

import com.example.dto.RequestDto;
import com.example.dto.ResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UnblockService {

    public List<ResponseDto> unblock(List<RequestDto> request){
        return request.stream()
                .map(dto -> new ResponseDto(dto.id(), isBlocked()))
                .toList();
    }

    private boolean isBlocked(){
        return new Random().nextInt(100) < 5;
    }

}
