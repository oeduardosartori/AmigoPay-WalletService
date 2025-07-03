package com.amigopay.wallet.wallet.service.impl;

import com.amigopay.wallet.common.enums.ValidationMessage;
import com.amigopay.wallet.exception.BusinessException;
import com.amigopay.wallet.wallet.dto.WalletResponse;
import com.amigopay.wallet.wallet.entity.Wallet;
import com.amigopay.wallet.wallet.mapper.WalletMapper;
import com.amigopay.wallet.wallet.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private WalletServiceImpl walletService;

    private final UUID userId = UUID.randomUUID();
    private final UUID payeeId = UUID.randomUUID();
    private final BigDecimal amount = BigDecimal.valueOf(50.0);

    // ---------- getBalance ----------

    @Test
    @DisplayName("Should return wallet balance when user exists")
    void shouldReturnBalance_WhenWalletExists() {
        Wallet wallet = Wallet.createWithBonus(userId);
        WalletResponse response = new WalletResponse(
                UUID.randomUUID(),
                userId,
                wallet.getBalance(),
                LocalDateTime.now()
        );

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletMapper.toResponse(wallet)).thenReturn(response);

        WalletResponse result = walletService.getBalance(userId);

        assertEquals(response, result);
        verify(walletRepository).findByUserId(userId);
        verify(walletMapper).toResponse(wallet);
    }

    @Test
    @DisplayName("Should throw when wallet not found")
    void shouldThrow_WhenWalletNotFound() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> walletService.getBalance(userId));

        assertEquals(ValidationMessage.WALLET_NOT_FOUND.key(), ex.getMessage());

        verify(walletRepository).findByUserId(userId);
    }

    // ---------- createWallet ----------

    @Test
    @DisplayName("Should create wallet when not already exists")
    void shouldCreateWallet_WhenWalletNotExists() {
        when(walletRepository.existsByUserId(userId)).thenReturn(false);
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);

        walletService.createWallet(userId);

        verify(walletRepository).existsByUserId(userId);
        verify(walletRepository).save(walletCaptor.capture());

        Wallet savedWallet = walletCaptor.getValue();
        assertEquals(userId, savedWallet.getUserId());
        assertNotNull(savedWallet.getBalance());
    }

    @Test
    @DisplayName("Should throw when wallet already exists")
    void shouldThrow_WhenWalletAlreadyExists() {
        when(walletRepository.existsByUserId(userId)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> walletService.createWallet(userId));
        assertEquals(ValidationMessage.WALLET_ALREADY_EXISTS.key(), ex.getMessage());

        verify(walletRepository).existsByUserId(userId);
        verify(walletRepository, never()).save(any());
    }

    // ---------- transfer ----------

    @Test
    @DisplayName("Should transfer amount between two valid wallets")
    void shouldTransferAmountSuccessfully() {
        Wallet payer = Wallet.createWithBonus(userId);
        Wallet payee = Wallet.createWithBonus(payeeId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(payer));
        when(walletRepository.findByUserId(payeeId)).thenReturn(Optional.of(payee));

        walletService.transfer(userId, payeeId, amount);

        assertEquals(new BigDecimal("950.00"), payer.getBalance());
        assertEquals(new BigDecimal("1050.00"), payee.getBalance());


        verify(walletRepository).saveAll(List.of(payer, payee));
    }

    @Test
    @DisplayName("Should throw when payer and payee are the same")
    void shouldThrow_WhenPayerIsEqualToPayee() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> walletService.transfer(userId, userId, amount));

        assertEquals(ValidationMessage.PAYER_EQUALS_PAYEE.key(), ex.getMessage());

        verify(walletRepository, never()).findByUserId(any());
        verify(walletRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should throw when payer wallet not found")
    void shouldThrow_WhenPayerWalletNotFound() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> walletService.transfer(userId, payeeId, amount));

        assertEquals(ValidationMessage.PAYER_WALLET_NOT_FOUND.key(), ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when payee wallet not found")
    void shouldThrow_WhenPayeeWalletNotFound() {
        Wallet payer = Wallet.createWithBonus(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(payer));
        when(walletRepository.findByUserId(payeeId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> walletService.transfer(userId, payeeId, amount));

        assertEquals(ValidationMessage.PAYEE_WALLET_NOT_FOUND.key(), ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when payer has insufficient balance")
    void shouldThrow_WhenPayerHasInsufficientBalance() {
        Wallet payer = Wallet.createWithBonus(userId);
        Wallet payee = Wallet.createWithBonus(payeeId);

        payer.debit(BigDecimal.valueOf(1000.00));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(payer));
        when(walletRepository.findByUserId(payeeId)).thenReturn(Optional.of(payee));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> walletService.transfer(userId, payeeId, amount));

        assertEquals(ValidationMessage.INSUFFICIENT_BALANCE_DEBIT.key(), ex.getMessage());

        verify(walletRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should maintain 2 decimal places on balance after transfer")
    void shouldMaintainBalancePrecision() {
        Wallet payer = Wallet.createWithBonus(userId);
        Wallet payee = Wallet.createWithBonus(payeeId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(payer));
        when(walletRepository.findByUserId(payeeId)).thenReturn(Optional.of(payee));

        walletService.transfer(userId, payeeId, new BigDecimal("0.015"));

        assertEquals(new BigDecimal("999.98"), payer.getBalance());
        assertEquals(new BigDecimal("1000.02"), payee.getBalance());
    }

    @Test
    @DisplayName("Should throw when transfer amount is negative or zero")
    void shouldThrow_WhenAmountIsNegativeOrZero() {
        Wallet payer = Wallet.createWithBonus(userId);
        Wallet payee = Wallet.createWithBonus(payeeId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(payer));
        when(walletRepository.findByUserId(payeeId)).thenReturn(Optional.of(payee));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                walletService.transfer(userId, payeeId, new BigDecimal("-10.00")));

        assertEquals(ValidationMessage.VALUE_MUST_BE_POSITIVE.key(), ex.getMessage());
        verify(walletRepository).findByUserId(userId);
        verify(walletRepository).findByUserId(payeeId);
    }
}
