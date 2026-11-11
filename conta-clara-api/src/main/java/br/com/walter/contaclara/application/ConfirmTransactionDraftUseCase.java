package br.com.walter.contaclara.application;

import br.com.walter.contaclara.domain.DraftRepository;
import br.com.walter.contaclara.domain.FinanceTransaction;
import br.com.walter.contaclara.domain.TransactionDraft;
import br.com.walter.contaclara.domain.TransactionRepository;
import br.com.walter.contaclara.domain.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConfirmTransactionDraftUseCase {
    private final DraftRepository draftRepository;
    private final TransactionRepository transactionRepository;

    public ConfirmTransactionDraftUseCase(DraftRepository draftRepository,
                                          TransactionRepository transactionRepository) {
        this.draftRepository = draftRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public FinanceTransaction execute(UUID draftId) {
        TransactionDraft draft = draftRepository.findByIdForUpdate(draftId)
                .orElseThrow(() -> new FinanceNotFoundException("Rascunho não encontrado."));

        if (draft.isConfirmed()) {
            return transactionRepository.findById(draft.transactionId())
                    .orElseThrow(() -> new IllegalStateException("Movimentação confirmada não encontrada."));
        }

        FinanceTransaction transaction = transactionRepository.save(new FinanceTransaction(
                UUID.randomUUID(), draft.description(), draft.amount(), draft.type(),
                draft.category(), draft.occurredOn()));

        draftRepository.save(draft.confirm(transaction.id()));
        return transaction;
    }
}
