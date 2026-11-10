package br.com.walter.designpatterns.api;

import br.com.walter.designpatterns.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        String accountId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant createdAt
) {
}
