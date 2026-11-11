package br.com.walter.contaclara.infrastructure.persistence;

import br.com.walter.contaclara.domain.DraftRepository;
import br.com.walter.contaclara.domain.TransactionDraft;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDraftRepository implements DraftRepository {
    private final DraftJpaRepository repository;

    public JpaDraftRepository(DraftJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TransactionDraft save(TransactionDraft draft) {
        return repository.save(DraftEntity.from(draft)).toDomain();
    }

    @Override
    public Optional<TransactionDraft> findByIdForUpdate(UUID id) {
        return repository.findByIdForUpdate(id).map(DraftEntity::toDomain);
    }
}
