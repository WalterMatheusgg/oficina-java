package br.com.walter.contaclara.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDraft(
        UUID id,
        String description,
        BigDecimal amount,
        TransactionType type,
        TransactionCategory category,
        LocalDate occurredOn,
        UUID transactionId,
        LocalDateTime createdAt
) {
    public TransactionDraft {
        if (id == null || description == null || description.isBlank()
                || amount == null || amount.signum() <= 0 || amount.scale() > 2
                || type == null || category == null || occurredOn == null || createdAt == null) {
            throw new IllegalArgumentException("Dados do rascunho inválidos.");
        }
        description = description.trim();
    }

    public boolean isConfirmed() {
        return transactionId != null;
    }

    public TransactionDraft confirm(UUID savedTransactionId) {
        if (savedTransactionId == null) {
            throw new IllegalArgumentException("A movimentação confirmada precisa de um identificador.");
        }
        return new TransactionDraft(id, description, amount, type, category, occurredOn,
                savedTransactionId, createdAt);
    }
}
