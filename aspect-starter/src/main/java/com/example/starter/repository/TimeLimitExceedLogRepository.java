package com.example.starter.repository;

import com.example.starter.entity.TimeLimitExceedLog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TimeLimitExceedLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(TimeLimitExceedLog log) {
        jdbcTemplate.update(
                "INSERT INTO time_limit_exceed_log (exceed_time, method_signature) VALUES (?, ?)",
                log.getTime(), log.getMethodSignature());
    }
}
