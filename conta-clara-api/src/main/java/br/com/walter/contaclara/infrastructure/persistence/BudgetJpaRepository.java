package br.com.walter.contaclara.infrastructure.persistence;

import br.com.walter.contaclara.domain.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetJpaRepository extends JpaRepository<BudgetEntity, String> {
    Optional<BudgetEntity> findByMonthAndCategory(String month, TransactionCategory category);
    List<BudgetEntity> findAllByMonthOrderByCategory(String month);
}
