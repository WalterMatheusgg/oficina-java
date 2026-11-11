package br.com.walter.contaclara.application;

import br.com.walter.contaclara.domain.DraftRepository;
import br.com.walter.contaclara.domain.TransactionCategory;
import br.com.walter.contaclara.domain.TransactionType;
import br.com.walter.contaclara.domain.TransactionDraft;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateTransactionDraftUseCase {
    private final DraftRepository draftRepository;

    public CreateTransactionDraftUseCase(DraftRepository draftRepository) {
        this.draftRepository = draftRepository;
    }

    @Transactional
    public TransactionDraft execute(String description, BigDecimal amount, TransactionType type, TransactionCategory category) {
        TransactionDraft draft = new TransactionDraft(UUID.randomUUID(), description, amount, type,
                category, LocalDate.now(), null, LocalDateTime.now());
        return draftRepository.save(draft);
    }
}
