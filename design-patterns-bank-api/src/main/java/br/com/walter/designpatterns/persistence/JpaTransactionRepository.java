package br.com.walter.designpatterns.persistence;

import br.com.walter.designpatterns.domain.TransactionRecord;
import br.com.walter.designpatterns.repository.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final TransactionJpaRepository transactions;

    public JpaTransactionRepository(TransactionJpaRepository transactions) {
        this.transactions = transactions;
    }

    @Override
    public Optional<TransactionRecord> findByAccountIdAndIdempotencyKey(
            String accountId,
            String idempotencyKey
    ) {
        return transactions.findByAccountIdAndIdempotencyKey(accountId, idempotencyKey)
                .map(TransactionEntity::toDomain);
    }

    @Override
    public TransactionRecord save(TransactionRecord transaction) {
        return transactions.save(TransactionEntity.from(transaction)).toDomain();
    }

    @Override
    public List<TransactionRecord> findAllByAccountId(String accountId) {
        return transactions.findAllByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
