package com.amigopay.wallet.common.util;

import com.amigopay.wallet.common.enums.ValidationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageResolver {

    private final MessageSource messageSource;

    public String resolve(ValidationMessage key) {
        return messageSource.getMessage(
                key.key(),
                null,
                LocaleContextHolder.getLocale()
        );
    }

    public String resolve(ValidationMessage key, Object... args) {
        return messageSource.getMessage(
                key.key(),
                args,
                LocaleContextHolder.getLocale()
        );
    }
}
