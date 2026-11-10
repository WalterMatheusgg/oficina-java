package br.com.walter.designpatterns.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
    Optional<TransactionEntity> findByAccountIdAndIdempotencyKey(String accountId, String idempotencyKey);

    List<TransactionEntity> findAllByAccountIdOrderByCreatedAtDesc(String accountId);
}
