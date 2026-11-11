package br.com.walter.contaclara.ai;

import br.com.walter.contaclara.application.CreateTransactionDraftUseCase;
import br.com.walter.contaclara.application.GetMonthlySummaryUseCase;
import br.com.walter.contaclara.domain.MonthlySummary;
import br.com.walter.contaclara.domain.TransactionCategory;
import br.com.walter.contaclara.domain.TransactionType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class FinanceAssistantTools {
    private final CreateTransactionDraftUseCase createDraft;
    private final GetMonthlySummaryUseCase getSummary;
    private final AtomicReference<UUID> createdDraftId = new AtomicReference<>();

    public FinanceAssistantTools(CreateTransactionDraftUseCase createDraft,
                                 GetMonthlySummaryUseCase getSummary) {
        this.createDraft = createDraft;
        this.getSummary = getSummary;
    }

    @Tool(name = "prepare_expense", description = "Prepara um rascunho de despesa quando a pessoa pedir claramente para registrar um gasto. Não confirma nem registra o lançamento.")
    public String prepareExpense(
            @ToolParam(description = "Descrição curta do gasto informado") String description,
            @ToolParam(description = "Valor positivo em reais, com no máximo duas casas decimais") BigDecimal amount,
            @ToolParam(description = "Categoria: FOOD, HOUSING, TRANSPORT, HEALTH, EDUCATION, LEISURE, SERVICES ou OTHER") String category) {
        TransactionCategory parsedCategory;
        try {
            parsedCategory = TransactionCategory.valueOf(category.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException exception) {
            return "Categoria não reconhecida. Peça à pessoa para escolher uma categoria válida.";
        }

        try {
            var draft = createDraft.execute(description, amount, TransactionType.EXPENSE, parsedCategory);
            createdDraftId.set(draft.id());
            return "Rascunho preparado. Código para confirmação: " + draft.id()
                    + ". Categoria: " + draft.category() + ". Valor: R$ " + draft.amount() + ".";
        } catch (IllegalArgumentException exception) {
            return "Não foi possível preparar esse gasto: " + exception.getMessage();
        }
    }

    @Tool(name = "get_monthly_budget_summary", description = "Consulta totais de receitas, despesas, saldo e limites por categoria para um mês.")
    public String monthlySummary(
            @ToolParam(description = "Mês a consultar no formato AAAA-MM") String month) {
        YearMonth parsedMonth;
        try {
            parsedMonth = YearMonth.parse(month);
        } catch (RuntimeException exception) {
            return "Mês inválido. Peça o mês no formato AAAA-MM.";
        }

        MonthlySummary summary = getSummary.execute(parsedMonth);
        return "Mês " + summary.month()
                + "; receitas R$ " + summary.income()
                + "; despesas R$ " + summary.expenses()
                + "; saldo R$ " + summary.balance()
                + "; categorias " + summary.categories();
    }

    public UUID createdDraftId() {
        return createdDraftId.get();
    }
}
