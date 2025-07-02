package com.amigopay.wallet.wallet.controller;

import com.amigopay.wallet.wallet.dto.WalletResponse;
import com.amigopay.wallet.wallet.service.WalletService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getBalance(@PathVariable @NotNull UUID userId) {
        WalletResponse response = walletService.getBalance(userId);
        return ResponseEntity.ok(response);
    }
}
