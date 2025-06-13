package com.example.starter.kafka;


import com.example.starter.dto.ErrorLogDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaClientProducer<T extends ErrorLogDto> {

    private final KafkaTemplate template;

    public KafkaClientProducer(KafkaTemplate template) {
        this.template = template;
    }

    public void sendWithErrorCode(String topic, String errorCode, Object o) throws Exception {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, o);
            record.headers().add("error_code", errorCode.getBytes(StandardCharsets.UTF_8));

            template.send(record).get();
        } catch (Exception ex) {
            throw ex;
        } finally {
            template.flush();
        }
    }

}
