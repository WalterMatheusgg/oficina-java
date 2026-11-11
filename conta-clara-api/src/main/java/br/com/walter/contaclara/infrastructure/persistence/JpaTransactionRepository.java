package br.com.walter.contaclara.infrastructure.persistence;

import br.com.walter.contaclara.domain.FinanceTransaction;
import br.com.walter.contaclara.domain.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final TransactionJpaRepository repository;

    public JpaTransactionRepository(TransactionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public FinanceTransaction save(FinanceTransaction transaction) {
        return repository.save(TransactionEntity.from(transaction)).toDomain();
    }

    @Override
    public Optional<FinanceTransaction> findById(UUID id) {
        return repository.findById(id).map(TransactionEntity::toDomain);
    }

    @Override
    public List<FinanceTransaction> findBetween(LocalDate start, LocalDate end) {
        return repository.findAllByOccurredOnBetweenOrderByOccurredOnDesc(start, end).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
