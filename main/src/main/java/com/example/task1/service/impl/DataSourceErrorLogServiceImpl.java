package com.example.task1.service.impl;

import com.example.task1.entity.DataSourceErrorLog;
import com.example.task1.repository.DataSourceErrorLogRepository;
import com.example.task1.service.DataSourceErrorLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {

    private final DataSourceErrorLogRepository repository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(MethodSignature signature, Exception ex) {
        DataSourceErrorLog log = DataSourceErrorLog.builder()
                .message(ex.getMessage())
                .stacktraceText(getStackTraceAsString(ex))
                .methodSignature(signature.toString())
                .build();
        repository.save(log);
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
