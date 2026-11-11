package br.com.walter.contaclara.api;

import br.com.walter.contaclara.domain.FinanceTransaction;
import br.com.walter.contaclara.domain.MonthlyBudget;
import br.com.walter.contaclara.domain.TransactionDraft;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public final class FinanceResponses {
    private FinanceResponses() {}

    public record Draft(
            UUID id, String description, BigDecimal amount, String type, String category,
            LocalDate occurredOn, boolean confirmed, UUID transactionId, LocalDateTime createdAt
    ) {
        public static Draft from(TransactionDraft draft) {
            return new Draft(draft.id(), draft.description(), draft.amount(), draft.type().name(),
                    draft.category().name(), draft.occurredOn(), draft.isConfirmed(),
                    draft.transactionId(), draft.createdAt());
        }
    }

    public record Transaction(
            UUID id, String description, BigDecimal amount, String type,
            String category, LocalDate occurredOn
    ) {
        public static Transaction from(FinanceTransaction transaction) {
            return new Transaction(transaction.id(), transaction.description(),
                    transaction.amount(), transaction.type().name(),
                    transaction.category().name(), transaction.occurredOn());
        }
    }

    public record Budget(YearMonth month, String category, BigDecimal limit) {
        public static Budget from(MonthlyBudget budget) {
            return new Budget(budget.month(), budget.category().name(), budget.limit());
        }
    }

    public record Transactions(List<Transaction> items) {}
}
