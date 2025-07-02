package com.amigopay.wallet.wallet.service.impl;

import com.amigopay.wallet.common.enums.ValidationMessage;
import com.amigopay.wallet.exception.BusinessException;
import com.amigopay.wallet.wallet.dto.WalletResponse;
import com.amigopay.wallet.wallet.entity.Wallet;
import com.amigopay.wallet.wallet.mapper.WalletMapper;
import com.amigopay.wallet.wallet.repository.WalletRepository;
import com.amigopay.wallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getBalance(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ValidationMessage.WALLET_NOT_FOUND));

        return walletMapper.toResponse(wallet);
    }

    @Override
    public void createWallet(UUID userId) {
        if (walletRepository.existsByUserId(userId)) {
            throw new BusinessException(ValidationMessage.WALLET_ALREADY_EXISTS);
        }

        Wallet wallet = Wallet.createWithBonus(userId);
        walletRepository.save(wallet);

        log.info("Wallet created successfully for userId={}, initial balance={}", userId, wallet.getBalance());
    }

    @Override
    public void transfer(UUID payerId, UUID payeeId, BigDecimal amount) {
        if (payerId.equals(payeeId)) {
            throw new BusinessException(ValidationMessage.PAYER_EQUALS_PAYEE);
        }

        Wallet payer = walletRepository.findByUserId(payerId)
                .orElseThrow(() -> new BusinessException(ValidationMessage.PAYER_WALLET_NOT_FOUND));

        Wallet payee = walletRepository.findByUserId(payeeId)
                .orElseThrow(() -> new BusinessException(ValidationMessage.PAYEE_WALLET_NOT_FOUND));

        payer.debit(amount);
        payee.credit(amount);

        walletRepository.saveAll(List.of(payer, payee));

        log.info("Transfer completed: payerId={} → payeeId={} | value={}", payerId, payeeId, amount);
    }
}
