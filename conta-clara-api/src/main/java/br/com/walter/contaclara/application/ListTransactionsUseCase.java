package br.com.walter.contaclara.application;

import br.com.walter.contaclara.domain.FinanceTransaction;
import br.com.walter.contaclara.domain.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
public class ListTransactionsUseCase {
    private final TransactionRepository transactionRepository;

    public ListTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<FinanceTransaction> execute(YearMonth month) {
        return transactionRepository.findBetween(month.atDay(1), month.atEndOfMonth());
    }
}
