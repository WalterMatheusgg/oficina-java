package br.com.walter.contaclara.infrastructure.persistence;

import br.com.walter.contaclara.domain.BudgetRepository;
import br.com.walter.contaclara.domain.MonthlyBudget;
import br.com.walter.contaclara.domain.TransactionCategory;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaBudgetRepository implements BudgetRepository {
    private final BudgetJpaRepository repository;

    public JpaBudgetRepository(BudgetJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public MonthlyBudget save(MonthlyBudget budget) {
        return repository.save(BudgetEntity.from(budget)).toDomain();
    }

    @Override
    public Optional<MonthlyBudget> findByMonthAndCategory(YearMonth month, TransactionCategory category) {
        return repository.findByMonthAndCategory(month.toString(), category).map(BudgetEntity::toDomain);
    }

    @Override
    public List<MonthlyBudget> findByMonth(YearMonth month) {
        return repository.findAllByMonthOrderByCategory(month.toString()).stream()
                .map(BudgetEntity::toDomain)
                .toList();
    }
}
