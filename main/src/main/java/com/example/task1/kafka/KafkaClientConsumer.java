package com.example.task1.kafka;

import com.example.task1.dto.ProcessedTransactionDto;
import com.example.task1.dto.TransactionDto;
import com.example.task1.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientConsumer {

    private final TransactionService transactionService;
    private final KafkaTemplate<String, ProcessedTransactionDto> kafkaTemplate;

    @KafkaListener(id = "${kafka.consumer.group-id}",
            topics = {"${kafka.consumer.topic.transactions-topic}"},
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload TransactionDto message,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            ProcessedTransactionDto processedTransaction = transactionService.createTransaction(message);
            kafkaTemplate.send("t1_demo_transaction_accept", processedTransaction);
            ack.acknowledge();
            log.info("Transaction processed successfully: {}", processedTransaction);
        } catch (EntityNotFoundException e) {
            log.error("Account not found: {}", e.getMessage());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage());
            throw e;
        }
    }
}
