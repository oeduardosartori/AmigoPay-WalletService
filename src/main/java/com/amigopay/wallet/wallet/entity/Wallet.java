package com.amigopay.wallet.wallet.entity;

import com.amigopay.wallet.common.enums.ValidationMessage;
import com.amigopay.wallet.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "wallets", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
})
public class Wallet {

    private static final BigDecimal INITIAL_BONUS = new BigDecimal("1000.00");
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true, updatable = false)
    private UUID userId;

    @Column(name = "balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    /**
     * Factory: creates a wallet with a default initial balance (R$ 1000.00).
     */
    public static Wallet createWithBonus(UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null.");

        return Wallet.builder()
                .userId(userId)
                .balance(INITIAL_BONUS)
                .build();
    }

    /**
     * Debits the amount from the wallet (validates and ensures sufficient balance).
     */
    public void debit(BigDecimal amount) {
        validateAmount(amount);

        if (hasInsufficientBalance(amount)) {
            throw new BusinessException(ValidationMessage.INSUFFICIENT_BALANCE_DEBIT);
        }

        this.balance = this.balance.subtract(amount).setScale(SCALE, ROUNDING);
    }

    /**
     * Credits value to the wallet.
     */
    public void credit(BigDecimal amount) {
        validateAmount(amount);
        this.balance = this.balance.add(amount).setScale(SCALE, ROUNDING);
    }

    /**
     * Checks if the value is invalid.
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException(ValidationMessage.VALUE_CANNOT_NULL);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ValidationMessage.VALUE_MUST_BE_POSITIVE);
        }
    }

    /**
     * Business rules: balance cannot be negative.
     */
    private boolean hasInsufficientBalance(BigDecimal amount) {
        return this.balance.subtract(amount).compareTo(BigDecimal.ZERO) < 0;
    }
}
