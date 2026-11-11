package br.com.walter.contaclara.domain;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyBudget(YearMonth month, TransactionCategory category, BigDecimal limit) {
    public MonthlyBudget {
        if (month == null || category == null || limit == null || limit.signum() <= 0 || limit.scale() > 2) {
            throw new IllegalArgumentException("O mês, a categoria e um limite positivo são obrigatórios.");
        }
    }
}
