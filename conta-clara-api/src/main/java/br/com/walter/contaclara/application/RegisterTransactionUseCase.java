package br.com.walter.contaclara.application;

import br.com.walter.contaclara.domain.FinanceTransaction;
import br.com.walter.contaclara.domain.TransactionRepository;
import br.com.walter.contaclara.domain.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class RegisterTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public RegisterTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public FinanceTransaction execute(String description, BigDecimal amount,
                                      TransactionType type, br.com.walter.contaclara.domain.TransactionCategory category,
                                      LocalDate occurredOn) {
        FinanceTransaction transaction = new FinanceTransaction(
                UUID.randomUUID(), description, amount, type, category, occurredOn);
        return transactionRepository.save(transaction);
    }
}
