package br.com.walter.designpatterns.repository;

import br.com.walter.designpatterns.domain.TransactionRecord;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Optional<TransactionRecord> findByAccountIdAndIdempotencyKey(String accountId, String idempotencyKey);

    TransactionRecord save(TransactionRecord transaction);

    List<TransactionRecord> findAllByAccountId(String accountId);
}
