package br.com.walter.designpatterns.persistence;

import br.com.walter.designpatterns.domain.TransactionRecord;
import br.com.walter.designpatterns.domain.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "transactions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_transactions_account_idempotency",
                columnNames = {"account_id", "idempotency_key"}
        )
)
public class TransactionEntity {
    @Id
    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "account_id", length = 64, nullable = false)
    private String accountId;

    @Column(name = "idempotency_key", length = 128, nullable = false)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", length = 20, nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TransactionEntity() {
    }

    public static TransactionEntity from(TransactionRecord record) {
        TransactionEntity entity = new TransactionEntity();
        entity.transactionId = record.transactionId();
        entity.accountId = record.accountId();
        entity.idempotencyKey = record.idempotencyKey();
        entity.type = record.type();
        entity.amount = record.amount();
        entity.balanceAfter = record.balanceAfter();
        entity.createdAt = record.createdAt();
        return entity;
    }

    public TransactionRecord toDomain() {
        return new TransactionRecord(
                transactionId,
                accountId,
                idempotencyKey,
                type,
                amount,
                balanceAfter,
                createdAt
        );
    }
}
