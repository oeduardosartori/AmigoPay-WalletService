package com.amigopay.wallet.messaging.producer.impl;

import com.amigopay.events.PaymentDoneEvent;
import com.amigopay.events.PaymentRejectedEvent;
import com.amigopay.wallet.messaging.producer.WalletEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaWalletEventPublisher implements WalletEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishPaymentDone(PaymentDoneEvent event) {
        kafkaTemplate.send("payment.done", event.paymentId().toString(), event);
        log.info("Event 'payment.done' published for paymentId={}", event.paymentId());
    }

    @Override
    public void publishPaymentRejected(PaymentRejectedEvent event) {
        kafkaTemplate.send("payment.rejected", event.paymentId().toString(), event);
        log.info("Event 'payment.rejected' published for paymentId={}, reason={}", event.paymentId(), event.rejectionReason());
    }
}
