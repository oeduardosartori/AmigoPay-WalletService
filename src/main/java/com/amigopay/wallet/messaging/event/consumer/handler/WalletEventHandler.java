package com.amigopay.wallet.messaging.event.consumer.handler;

import com.amigopay.events.UserCreatedEvent;

import com.amigopay.wallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventHandler {

    private final WalletService walletService;

    public void processUserCreated(UserCreatedEvent event) {
        log.debug("Processing wallet creation for userId={}", event.id());
        walletService.createWallet(event.id());
    }
}