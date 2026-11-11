package br.com.walter.contaclara.domain;

import java.util.Optional;
import java.util.UUID;

public interface DraftRepository {
    TransactionDraft save(TransactionDraft draft);
    Optional<TransactionDraft> findByIdForUpdate(UUID id);
}
