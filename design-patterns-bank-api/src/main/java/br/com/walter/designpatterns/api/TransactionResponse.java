package br.com.walter.designpatterns.api;

import br.com.walter.designpatterns.domain.TransactionType;

import java.math.BigDecimal;

public record TransactionResponse(
        String accountId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter
) {
}
