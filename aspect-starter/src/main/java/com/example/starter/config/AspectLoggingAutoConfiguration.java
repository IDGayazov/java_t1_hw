package com.example.starter.config;


import com.example.starter.aop.ErrorLogging;
import com.example.starter.aop.TimeMetricLogging;
import com.example.starter.kafka.KafkaClientProducer;
import com.example.starter.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnClass({ErrorLogging.class, TimeMetricLogging.class, KafkaClientProducer.class})
@AutoConfigureAfter({JdbcConfig.class, JdbcServiceConfig.class})
public class AspectLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ErrorLogging errorLogging(@Qualifier("dataSourceErrorService") ErrorLogService errorLogService,
                                           KafkaClientProducer kafkaClientProducer) {
        return new ErrorLogging(errorLogService, kafkaClientProducer);
    }

    @Bean
    @ConditionalOnMissingBean
    public TimeMetricLogging timeMetricLogging(@Qualifier("timeLimitExceedErrorService") ErrorLogService errorLogService,
                                           KafkaClientProducer kafkaClientProducer) {
        return new TimeMetricLogging(errorLogService, kafkaClientProducer);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaClientProducer kafkaClientProducer(KafkaTemplate kafkaTemplate) {
        return new KafkaClientProducer(kafkaTemplate);
    }
}