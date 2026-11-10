package br.com.walter.designpatterns.strategy;

import br.com.walter.designpatterns.domain.Account;
import br.com.walter.designpatterns.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositStrategy implements TransactionStrategy {
    @Override
    public TransactionType type() {
        return TransactionType.DEPOSIT;
    }

    @Override
    public BigDecimal execute(Account account, BigDecimal amount) {
        return account.deposit(amount);
    }
}
