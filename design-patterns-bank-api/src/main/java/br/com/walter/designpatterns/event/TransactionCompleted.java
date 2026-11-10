package br.com.walter.designpatterns.event;

import br.com.walter.designpatterns.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Evento publicado depois que uma operação altera o saldo da conta.
 */
public record TransactionCompleted(
        UUID transactionId,
        String accountId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant createdAt
) {
}
