package br.com.walter.designpatterns.strategy;

import br.com.walter.designpatterns.domain.Account;
import br.com.walter.designpatterns.domain.TransactionType;

import java.math.BigDecimal;

public interface TransactionStrategy {
    TransactionType type();

    BigDecimal execute(Account account, BigDecimal amount);
}
