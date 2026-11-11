package br.com.walter.contaclara.infrastructure.persistence;

import br.com.walter.contaclara.domain.FinanceTransaction;
import br.com.walter.contaclara.domain.TransactionCategory;
import br.com.walter.contaclara.domain.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "finance_transactions", indexes = {
        @Index(name = "idx_finance_transactions_date", columnList = "occurred_on")
})
public class TransactionEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private TransactionCategory category;

    @Column(name = "occurred_on", nullable = false)
    private LocalDate occurredOn;

    protected TransactionEntity() {}

    private TransactionEntity(FinanceTransaction transaction) {
        this.id = transaction.id();
        this.description = transaction.description();
        this.amount = transaction.amount();
        this.type = transaction.type();
        this.category = transaction.category();
        this.occurredOn = transaction.occurredOn();
    }

    static TransactionEntity from(FinanceTransaction transaction) {
        return new TransactionEntity(transaction);
    }

    FinanceTransaction toDomain() {
        return new FinanceTransaction(id, description, amount, type, category, occurredOn);
    }

    UUID getId() { return id; }
}
