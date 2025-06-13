package com.example.starter.config;

import com.example.starter.config.property.ErrorLoggingKafkaProperties;
import com.example.starter.dto.ErrorLogDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "error.logging.kafka.enabled", havingValue = "true")
@EnableConfigurationProperties(ErrorLoggingKafkaProperties.class)
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, ErrorLogDto> errorLogProducerFactory(
            ErrorLoggingKafkaProperties properties) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                properties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG,
                properties.getProducer().getRetries());
        configProps.put(ProducerConfig.ACKS_CONFIG,
                properties.getProducer().getAcks());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ErrorLogDto> errorLogKafkaTemplate(
            ProducerFactory<String, ErrorLogDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
