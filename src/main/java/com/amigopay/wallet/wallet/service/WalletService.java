package com.amigopay.wallet.wallet.service;

import com.amigopay.wallet.wallet.dto.WalletResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    WalletResponse getBalance(UUID userId);

    void createWallet(UUID userId);

    void transfer(UUID payerId, UUID payeeId, BigDecimal amount);
}
