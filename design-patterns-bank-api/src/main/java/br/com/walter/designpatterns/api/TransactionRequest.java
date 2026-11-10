package br.com.walter.designpatterns.api;

import br.com.walter.designpatterns.domain.TransactionType;

import java.math.BigDecimal;

public record TransactionRequest(TransactionType type, BigDecimal amount) {
}
