package br.com.walter.contaclara.domain;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record MonthlySummary(
        YearMonth month,
        BigDecimal income,
        BigDecimal expenses,
        BigDecimal balance,
        List<CategorySpending> categories
) {
    public MonthlySummary {
        categories = List.copyOf(categories);
    }

    public record CategorySpending(
            TransactionCategory category,
            BigDecimal spent,
            BigDecimal budgetLimit,
            BigDecimal usedPercent,
            BudgetStatus status
    ) {}

    public enum BudgetStatus {
        NO_LIMIT,
        ON_TRACK,
        NEAR_LIMIT,
        OVER_LIMIT
    }
}
