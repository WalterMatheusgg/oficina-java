package br.com.walter.designpatterns.persistence;

import br.com.walter.designpatterns.domain.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    @Column(name = "account_id", length = 64, nullable = false)
    private String id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    @Column(nullable = false)
    private long version;

    protected AccountEntity() {
    }

    public AccountEntity(String id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public static AccountEntity from(Account account) {
        return new AccountEntity(account.id(), account.balance());
    }

    public Account toDomain() {
        return new Account(id, balance);
    }

    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

    public String id() {
        return id;
    }
}
