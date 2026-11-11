package br.com.walter.contaclara.application;

import br.com.walter.contaclara.domain.BudgetRepository;
import br.com.walter.contaclara.domain.MonthlyBudget;
import br.com.walter.contaclara.domain.TransactionCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
public class SetMonthlyBudgetUseCase {
    private final BudgetRepository budgetRepository;

    public SetMonthlyBudgetUseCase(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public MonthlyBudget execute(YearMonth month, TransactionCategory category, BigDecimal limit) {
        return budgetRepository.save(new MonthlyBudget(month, category, limit));
    }
}
