package br.com.walter.contaclara.api;

import br.com.walter.contaclara.application.SetMonthlyBudgetUseCase;
import br.com.walter.contaclara.domain.TransactionCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/budgets")
@Validated
public class BudgetController {
    private final SetMonthlyBudgetUseCase setBudget;

    public BudgetController(SetMonthlyBudgetUseCase setBudget) {
        this.setBudget = setBudget;
    }

    @PutMapping("/{month}/{category}")
    public FinanceResponses.Budget set(
            @PathVariable @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "Use o formato AAAA-MM.") String month,
            @PathVariable TransactionCategory category,
            @Valid @RequestBody MonthlyBudgetRequest request) {
        return FinanceResponses.Budget.from(setBudget.execute(
                YearMonth.parse(month), category, request.limit()));
    }
}
