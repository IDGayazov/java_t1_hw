package com.example.starter.service.impl;

import com.example.starter.dto.ErrorLogDto;
import com.example.starter.entity.TimeLimitExceedLog;
import com.example.starter.repository.TimeLimitExceedLogRepository;
import com.example.starter.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Qualifier("timeLimitExceedErrorService")
@RequiredArgsConstructor
public class TimeLimitErrorLogService implements ErrorLogService {

    private final TimeLimitExceedLogRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(ErrorLogDto logDto) {
        TimeLimitExceedLog log = TimeLimitExceedLog.builder()
                .time(logDto.time())
                .methodSignature(logDto.methodName())
                .build();
        repository.save(log);
    }
}
