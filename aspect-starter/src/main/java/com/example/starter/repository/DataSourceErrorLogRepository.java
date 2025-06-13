package com.example.starter.repository;

import com.example.starter.entity.DataSourceErrorLog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DataSourceErrorLogRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
        INSERT INTO data_source_error_log 
        (message, stacktrace_text, method_signature) 
        VALUES (?, ?, ?)
        """;

    public void save(DataSourceErrorLog log) {
        jdbcTemplate.update(INSERT_SQL,
                log.getMessage(),
                log.getStacktraceText(),
                log.getMethodSignature());
    }
}
