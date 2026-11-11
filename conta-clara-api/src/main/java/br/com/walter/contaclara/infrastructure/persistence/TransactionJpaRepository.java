package br.com.walter.contaclara.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByOccurredOnBetweenOrderByOccurredOnDesc(LocalDate start, LocalDate end);
}
