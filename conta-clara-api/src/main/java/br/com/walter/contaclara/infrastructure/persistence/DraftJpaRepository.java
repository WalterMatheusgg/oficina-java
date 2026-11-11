package br.com.walter.contaclara.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DraftJpaRepository extends JpaRepository<DraftEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select draft from DraftEntity draft where draft.id = :id")
    Optional<DraftEntity> findByIdForUpdate(@Param("id") UUID id);
}
