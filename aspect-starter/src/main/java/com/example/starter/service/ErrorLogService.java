package com.example.starter.service;


import com.example.starter.dto.ErrorLogDto;

import java.io.PrintWriter;
import java.io.StringWriter;

public interface ErrorLogService {
    void saveErrorLog(ErrorLogDto logDto);

    default String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
