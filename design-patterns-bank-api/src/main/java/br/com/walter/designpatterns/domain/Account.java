package br.com.walter.designpatterns.domain;

import java.math.BigDecimal;

public class Account {
    private final String id;
    private BigDecimal balance;

    public Account(String id, BigDecimal openingBalance) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Account id is required.");
        }
        if (openingBalance == null || openingBalance.signum() < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.id = id;
        this.balance = openingBalance;
    }

    public String id() {
        return id;
    }

    public synchronized BigDecimal balance() {
        return balance;
    }

    public synchronized BigDecimal deposit(BigDecimal amount) {
        validatePositiveAmount(amount);
        balance = balance.add(amount);
        return balance;
    }

    public synchronized BigDecimal withdraw(BigDecimal amount) {
        validatePositiveAmount(amount);
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        balance = balance.subtract(amount);
        return balance;
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}
