package com.example.task1.service.impl;

import com.example.task1.dto.ErrorLogDto;
import com.example.task1.entity.TimeLimitExceedLog;
import com.example.task1.repository.TimeLimitExceedLogRepository;
import com.example.task1.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Qualifier("timeLimitExceedErrorService")
public class TimeLimitErrorLogService implements ErrorLogService {

    private final TimeLimitExceedLogRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(ErrorLogDto logDto) {
        TimeLimitExceedLog log = TimeLimitExceedLog.builder()
                .time(logDto.time())
                .methodSignature(logDto.methodSignature().toString())
                .build();
        repository.save(log);
    }
}
