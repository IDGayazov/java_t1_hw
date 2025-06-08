package com.example.task1.kafka;

import com.example.task1.dto.ErrorLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientProducer<T extends ErrorLogDto> {

    private final KafkaTemplate template;

    public void sendWithErrorCode(String topic, String errorCode, Object o) throws Exception {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, o);
            record.headers().add("error_code", errorCode.getBytes(StandardCharsets.UTF_8));

            template.send(record).get();
        } catch (Exception ex) {
            log.error("Failed to send Kafka message", ex);
            throw ex;
        } finally {
            template.flush();
        }
    }

}
