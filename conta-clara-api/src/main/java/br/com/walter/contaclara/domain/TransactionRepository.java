package br.com.walter.contaclara.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    FinanceTransaction save(FinanceTransaction transaction);
    Optional<FinanceTransaction> findById(UUID id);
    List<FinanceTransaction> findBetween(LocalDate start, LocalDate end);
}
