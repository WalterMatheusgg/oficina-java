package br.com.walter.contaclara.application;

import br.com.walter.contaclara.domain.BudgetRepository;
import br.com.walter.contaclara.domain.FinanceTransaction;
import br.com.walter.contaclara.domain.MonthlyBudget;
import br.com.walter.contaclara.domain.MonthlySummary;
import br.com.walter.contaclara.domain.TransactionCategory;
import br.com.walter.contaclara.domain.TransactionRepository;
import br.com.walter.contaclara.domain.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetMonthlySummaryUseCase {
    private static final BigDecimal ATTENTION_THRESHOLD = new BigDecimal("80");

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public GetMonthlySummaryUseCase(TransactionRepository transactionRepository,
                                    BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    @Transactional(readOnly = true)
    public MonthlySummary execute(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<FinanceTransaction> transactions = transactionRepository.findBetween(start, end);

        BigDecimal income = sum(transactions, TransactionType.INCOME);
        BigDecimal expenses = sum(transactions, TransactionType.EXPENSE);
        Map<TransactionCategory, BigDecimal> budgets = budgetRepository.findByMonth(month).stream()
                .collect(Collectors.toMap(MonthlyBudget::category, MonthlyBudget::limit));

        List<MonthlySummary.CategorySpending> categories = EnumSet.allOf(TransactionCategory.class).stream()
                .map(category -> categorySummary(category, transactions, budgets.get(category)))
                .toList();

        return new MonthlySummary(month, income, expenses, income.subtract(expenses), categories);
    }

    private BigDecimal sum(List<FinanceTransaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(transaction -> transaction.type() == type)
                .map(FinanceTransaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private MonthlySummary.CategorySpending categorySummary(
            TransactionCategory category, List<FinanceTransaction> transactions, BigDecimal limit) {
        BigDecimal spent = transactions.stream()
                .filter(transaction -> transaction.type() == TransactionType.EXPENSE)
                .filter(transaction -> transaction.category() == category)
                .map(FinanceTransaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (limit == null) {
            return new MonthlySummary.CategorySpending(category, spent, null, BigDecimal.ZERO,
                    MonthlySummary.BudgetStatus.NO_LIMIT);
        }

        BigDecimal percentage = spent.multiply(new BigDecimal("100"))
                .divide(limit, 2, RoundingMode.HALF_UP);
        MonthlySummary.BudgetStatus status = spent.compareTo(limit) >= 0
                ? MonthlySummary.BudgetStatus.OVER_LIMIT
                : percentage.compareTo(ATTENTION_THRESHOLD) >= 0
                    ? MonthlySummary.BudgetStatus.NEAR_LIMIT
                    : MonthlySummary.BudgetStatus.ON_TRACK;

        return new MonthlySummary.CategorySpending(category, spent, limit, percentage, status);
    }
}
