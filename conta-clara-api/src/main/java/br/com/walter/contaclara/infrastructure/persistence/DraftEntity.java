package br.com.walter.contaclara.infrastructure.persistence;

import br.com.walter.contaclara.domain.TransactionCategory;
import br.com.walter.contaclara.domain.TransactionDraft;
import br.com.walter.contaclara.domain.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_drafts", indexes = @Index(name = "idx_transaction_drafts_created_at", columnList = "created_at"))
public class DraftEntity {
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

    @Column(name = "transaction_id", unique = true)
    private UUID transactionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected DraftEntity() {}

    private DraftEntity(TransactionDraft draft) {
        id = draft.id();
        description = draft.description();
        amount = draft.amount();
        type = draft.type();
        category = draft.category();
        occurredOn = draft.occurredOn();
        transactionId = draft.transactionId();
        createdAt = draft.createdAt();
    }

    static DraftEntity from(TransactionDraft draft) {
        return new DraftEntity(draft);
    }

    TransactionDraft toDomain() {
        return new TransactionDraft(id, description, amount, type, category, occurredOn, transactionId, createdAt);
    }
}
