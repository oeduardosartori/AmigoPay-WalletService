package com.amigopay.wallet.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletResponse(
        UUID walletId,
        UUID userId,
        BigDecimal balance,
        LocalDateTime updatedAt
) {}
