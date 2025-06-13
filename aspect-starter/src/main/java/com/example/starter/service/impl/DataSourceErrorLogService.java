package com.example.starter.service.impl;

import com.example.starter.dto.ErrorLogDto;
import com.example.starter.entity.DataSourceErrorLog;
import com.example.starter.repository.DataSourceErrorLogRepository;
import com.example.starter.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Qualifier("dataSourceErrorService")
@RequiredArgsConstructor
public class DataSourceErrorLogService implements ErrorLogService {

    private final DataSourceErrorLogRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(ErrorLogDto logDto) {
        DataSourceErrorLog log = DataSourceErrorLog.builder()
                .message(logDto.exception().getMessage())
                .stacktraceText(getStackTraceAsString(logDto.exception()))
                .methodSignature(logDto.methodName())
                .build();
        repository.save(log);
    }
}
