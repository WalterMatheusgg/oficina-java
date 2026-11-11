package br.com.walter.contaclara.api;

import br.com.walter.contaclara.application.GetMonthlySummaryUseCase;
import br.com.walter.contaclara.application.ListTransactionsUseCase;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class TransactionController {
    private final ListTransactionsUseCase listTransactions;
    private final GetMonthlySummaryUseCase getSummary;

    public TransactionController(ListTransactionsUseCase listTransactions,
                                 GetMonthlySummaryUseCase getSummary) {
        this.listTransactions = listTransactions;
        this.getSummary = getSummary;
    }

    @GetMapping("/transactions")
    public FinanceResponses.Transactions list(
            @RequestParam @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "Use o formato AAAA-MM.") String month) {
        List<FinanceResponses.Transaction> items = listTransactions.execute(YearMonth.parse(month)).stream()
                .map(FinanceResponses.Transaction::from)
                .toList();
        return new FinanceResponses.Transactions(items);
    }

    @GetMapping("/summary/{month}")
    public br.com.walter.contaclara.domain.MonthlySummary summary(
            @PathVariable @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "Use o formato AAAA-MM.") String month) {
        return getSummary.execute(YearMonth.parse(month));
    }
}
