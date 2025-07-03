package com.amigopay.wallet.messaging.event.producer;


import com.amigopay.events.PaymentDoneEvent;
import com.amigopay.events.PaymentRejectedEvent;

public interface WalletEventPublisher {

    void publishPaymentDone(PaymentDoneEvent event);

    void publishPaymentRejected(PaymentRejectedEvent event);
}
