package br.com.walter.contaclara.infrastructure.persistence;

import br.com.walter.contaclara.domain.MonthlyBudget;
import br.com.walter.contaclara.domain.TransactionCategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "monthly_budgets",
        uniqueConstraints = @UniqueConstraint(name = "uk_monthly_budgets_month_category",
                columnNames = {"budget_month", "category"}))
public class BudgetEntity {
    @Id
    private String id;

    @Column(name = "budget_month", nullable = false, length = 7)
    private String month;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private TransactionCategory category;

    @Column(name = "spending_limit", nullable = false, precision = 12, scale = 2)
    private BigDecimal limit;

    protected BudgetEntity() {}

    private BudgetEntity(MonthlyBudget budget) {
        id = budget.month() + "-" + budget.category();
        month = budget.month().toString();
        category = budget.category();
        limit = budget.limit();
    }

    static BudgetEntity from(MonthlyBudget budget) {
        return new BudgetEntity(budget);
    }

    MonthlyBudget toDomain() {
        return new MonthlyBudget(YearMonth.parse(month), category, limit);
    }
}
