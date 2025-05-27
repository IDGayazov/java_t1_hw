package com.example.task1.service;

import org.aspectj.lang.reflect.MethodSignature;

public interface DataSourceErrorLogService {
    void saveErrorLog(MethodSignature signature, Exception exception);
}
