package com.example.starter.config;

import com.example.starter.repository.DataSourceErrorLogRepository;
import com.example.starter.repository.TimeLimitExceedLogRepository;
import com.example.starter.service.ErrorLogService;
import com.example.starter.service.impl.DataSourceErrorLogService;
import com.example.starter.service.impl.TimeLimitErrorLogService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(DataSource.class)
public class JdbcServiceConfig {

    @Bean
    @Qualifier("dataSourceErrorService")
    public ErrorLogService dataSourceErrorLogService(DataSourceErrorLogRepository repository) {
        return new DataSourceErrorLogService(repository);
    }

    @Bean
    @Qualifier("timeLimitExceedErrorService")
    public TimeLimitErrorLogService timeLimitErrorLogService(TimeLimitExceedLogRepository repository) {
        return new TimeLimitErrorLogService(repository);
    }
}
