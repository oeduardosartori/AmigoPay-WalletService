package com.amigopay.wallet.exception;

import com.amigopay.wallet.common.enums.ValidationMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final ValidationMessage validationMessage;

    public BusinessException(ValidationMessage validationMessage) {
        super(validationMessage.key()); // usado apenas como fallback
        this.validationMessage = validationMessage;
    }

    public BusinessException(ValidationMessage validationMessage, Throwable cause) {
        super(validationMessage.key(), cause);
        this.validationMessage = validationMessage;
    }
}