package br.com.walter.contaclara.api;

import br.com.walter.contaclara.application.ConfirmTransactionDraftUseCase;
import br.com.walter.contaclara.application.CreateTransactionDraftUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/drafts")
public class DraftController {
    private final CreateTransactionDraftUseCase createDraft;
    private final ConfirmTransactionDraftUseCase confirmDraft;

    public DraftController(CreateTransactionDraftUseCase createDraft,
                           ConfirmTransactionDraftUseCase confirmDraft) {
        this.createDraft = createDraft;
        this.confirmDraft = confirmDraft;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceResponses.Draft create(@Valid @RequestBody TransactionDraftRequest request) {
        return FinanceResponses.Draft.from(createDraft.execute(
                request.description(), request.amount(), request.type(), request.category()));
    }

    @PostMapping("/{id}/confirm")
    public FinanceResponses.Transaction confirm(@PathVariable UUID id) {
        return FinanceResponses.Transaction.from(confirmDraft.execute(id));
    }
}
