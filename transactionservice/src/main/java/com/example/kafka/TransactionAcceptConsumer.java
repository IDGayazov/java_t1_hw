package com.example.kafka;

import com.example.model.dto.TransactionDto;
import com.example.service.TransactionProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionAcceptConsumer {

    private final TransactionProcessService processorService;

    @KafkaListener(
            topics = "${kafka.consumer.topic.transactions-accept}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenTransactionAccept(
            @Payload TransactionDto transaction,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        log.info("Received transaction for processing: {}", transaction);
        try {
            processorService.processTransaction(transaction);
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage());
            // Можно добавить обработку ошибок и отправку в DLQ
        }
    }
}
