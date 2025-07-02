package com.amigopay.wallet.common.enums;

public enum ValidationMessage {

    // Wallet Status
    WALLET_NOT_FOUND("validation.wallet.not-found"),
    WALLET_ALREADY_EXISTS("validation.wallet.already.exists"),
    VALUE_CANNOT_NULL("validation.value.cannot.null"),
    VALUE_MUST_BE_POSITIVE("validation.value.positive"),
    INSUFFICIENT_BALANCE_DEBIT("validation.insufficient.balance.debit"),
    PAYER_EQUALS_PAYEE("validation.payer.equals.payee"),
    PAYER_WALLET_NOT_FOUND("validation.payer.wallet.not-found"),
    PAYEE_WALLET_NOT_FOUND("validation.payee.wallet.not-found"),

    // Internal error
    INTERNAL_ERROR("validation.internal.error"),

    // Invalid credentials
    INVALID_CREDENTIALS("validation.invalid.credentials");

    private final String key;

    ValidationMessage(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
