package com.amigopay.wallet.messaging.consumer;

import com.amigopay.events.PaymentInitiatedEvent;
import com.amigopay.wallet.messaging.consumer.handler.WalletEventHandler;
import com.amigopay.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener {

    private final WalletEventHandler eventHandler;

    @KafkaListener(
            topics = "user.created",
            groupId = "wallet-consumer",
            containerFactory = "userCreatedKafkaListenerContainerFactory"
    )
    public void handleUserCreated(
            ConsumerRecord<String, UserCreatedEvent> record,
            Acknowledgment ack
    ) {
        UserCreatedEvent event = record.value();
        log.info("Received 'user.created' event for userId={}", event.id());

        try {
            eventHandler.processUserCreated(event);
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process 'user.created' for userId={}", event.id(), ex);
        }
    }

    @KafkaListener(
            topics = "payment.initiated",
            groupId = "wallet-consumer",
            containerFactory = "paymentInitiatedKafkaListenerContainerFactory"
    )
    public void handlePaymentInitiated(
            ConsumerRecord<String, PaymentInitiatedEvent> record,
            Acknowledgment ack
    ) {
        PaymentInitiatedEvent event = record.value();
        log.info("Received 'payment.initiated' event for paymentId={}", event.paymentId());

        try {
            eventHandler.processPaymentInitiated(event);
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process 'payment.initiated' for paymentId={}", event.paymentId(), ex);
        }
    }
}
