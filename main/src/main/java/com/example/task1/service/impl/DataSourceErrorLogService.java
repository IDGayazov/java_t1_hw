package com.example.task1.service.impl;

import com.example.task1.dto.ErrorLogDto;
import com.example.task1.entity.DataSourceErrorLog;
import com.example.task1.repository.DataSourceErrorLogRepository;
import com.example.task1.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Qualifier("dataSourceErrorService")
public class DataSourceErrorLogService implements ErrorLogService {

    private final DataSourceErrorLogRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(ErrorLogDto logDto) {
        DataSourceErrorLog log = DataSourceErrorLog.builder()
                .message(logDto.exception().getMessage())
                .stacktraceText(getStackTraceAsString(logDto.exception()))
                .methodSignature(logDto.methodSignature().toString())
                .build();
        repository.save(log);
    }
}
