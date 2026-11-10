package br.com.walter.designpatterns.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionRecord(
        UUID transactionId,
        String accountId,
        String idempotencyKey,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant createdAt
) {
}
