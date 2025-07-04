package com.amigopay.wallet.messaging.consumer.handler;

import com.amigopay.events.PaymentDoneEvent;
import com.amigopay.events.PaymentInitiatedEvent;
import com.amigopay.events.PaymentRejectedEvent;
import com.amigopay.events.UserCreatedEvent;

import com.amigopay.events.enums.PaymentStatus;
import com.amigopay.wallet.exception.BusinessException;
import com.amigopay.wallet.messaging.producer.WalletEventPublisher;
import com.amigopay.wallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventHandler {

    private final WalletService walletService;
    private final WalletEventPublisher publisher;

    public void processUserCreated(UserCreatedEvent event) {
        log.debug("Processing wallet creation for userId={}", event.id());
        walletService.createWallet(event.id());
    }

    public void processPaymentInitiated(PaymentInitiatedEvent event) {
        UUID payerId = event.payerId();
        UUID payeeId = event.payeeId();
        BigDecimal amount = event.amount();

        try {
            walletService.transfer(payerId, payeeId, amount);

            PaymentDoneEvent done = new PaymentDoneEvent(
              event.paymentId(),
                    payerId,
                    payeeId,
                    amount,
                    PaymentStatus.COMPLETED,
                    LocalDateTime.now()
            );
            publisher.publishPaymentDone(done);
        } catch (BusinessException bex) {
            PaymentRejectedEvent rejected = new PaymentRejectedEvent(
                    event.paymentId(),
                    payerId,
                    payeeId,
                    amount,
                    PaymentStatus.REJECTED,
                    LocalDateTime.now(),
                    bex.getMessage()
            );
            publisher.publishPaymentRejected(rejected);
        }
    }
}