package br.com.walter.contaclara.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FinanceTransaction(
        UUID id,
        String description,
        BigDecimal amount,
        TransactionType type,
        TransactionCategory category,
        LocalDate occurredOn
) {
    public FinanceTransaction {
        if (id == null || description == null || description.isBlank()
                || amount == null || amount.signum() <= 0
                || type == null || category == null || occurredOn == null) {
            throw new IllegalArgumentException("Dados da movimentação inválidos.");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("O valor deve ter no máximo duas casas decimais.");
        }
        description = description.trim();
    }
}
