package com.example.starter.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "error.logging.kafka")
@Getter
@Setter
public class ErrorLoggingKafkaProperties {
    private String topic;
    private String bootstrapServers;
    private Producer producer = new Producer();

    @Getter
    @Setter
    public static class Producer {
        private int retries;
        private String acks;
    }
}
