package br.com.walter.designpatterns.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    void depositAddsAmountToBalance() {
        Account account = new Account("ACC-TEST", new BigDecimal("100.00"));

        BigDecimal balanceAfter = account.deposit(new BigDecimal("25.50"));

        assertThat(balanceAfter).isEqualByComparingTo("125.50");
        assertThat(account.balance()).isEqualByComparingTo("125.50");
    }

    @Test
    void withdrawSubtractsAmountWhenFundsAreAvailable() {
        Account account = new Account("ACC-TEST", new BigDecimal("100.00"));

        BigDecimal balanceAfter = account.withdraw(new BigDecimal("30.00"));

        assertThat(balanceAfter).isEqualByComparingTo("70.00");
        assertThat(account.balance()).isEqualByComparingTo("70.00");
    }

    @Test
    void rejectsZeroOrNegativeAmountsWithoutChangingBalance() {
        Account account = new Account("ACC-TEST", new BigDecimal("100.00"));

        assertThatThrownBy(() -> account.deposit(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be greater than zero.");
        assertThatThrownBy(() -> account.withdraw(new BigDecimal("-1.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be greater than zero.");

        assertThat(account.balance()).isEqualByComparingTo("100.00");
    }

    @Test
    void rejectsWithdrawalAboveBalanceWithoutChangingBalance() {
        Account account = new Account("ACC-TEST", new BigDecimal("100.00"));

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("100.01")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient funds.");

        assertThat(account.balance()).isEqualByComparingTo("100.00");
    }
}
