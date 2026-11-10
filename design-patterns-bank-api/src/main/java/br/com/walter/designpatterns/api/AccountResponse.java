package br.com.walter.designpatterns.api;

import java.math.BigDecimal;

public record AccountResponse(String accountId, BigDecimal balance) {
}
