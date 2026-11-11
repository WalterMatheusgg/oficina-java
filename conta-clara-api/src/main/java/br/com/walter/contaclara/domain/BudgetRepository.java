package br.com.walter.contaclara.domain;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository {
    MonthlyBudget save(MonthlyBudget budget);
    Optional<MonthlyBudget> findByMonthAndCategory(YearMonth month, TransactionCategory category);
    List<MonthlyBudget> findByMonth(YearMonth month);
}
