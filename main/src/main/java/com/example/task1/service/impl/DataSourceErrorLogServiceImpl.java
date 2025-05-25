package com.example.task1.service.impl;

import com.example.task1.entity.DataSourceErrorLog;
import com.example.task1.repository.DataSourceErrorLogRepository;
import com.example.task1.service.DataSourceErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {

    private final DataSourceErrorLogRepository repository;

    @Override
    public DataSourceErrorLog saveErrorLog(DataSourceErrorLog errorLog) {
        return repository.save(errorLog);
    }
}
