package com.example.starter.config;

import com.example.starter.repository.DataSourceErrorLogRepository;
import com.example.starter.repository.TimeLimitExceedLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    @Bean
    public DataSourceErrorLogRepository dataSourceErrorLogRepository(JdbcTemplate jdbcTemplate){
        return new DataSourceErrorLogRepository(jdbcTemplate);
    }

    @Bean
    public TimeLimitExceedLogRepository timeLimitExceedLogRepository(JdbcTemplate jdbcTemplate){
        return new TimeLimitExceedLogRepository(jdbcTemplate);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
