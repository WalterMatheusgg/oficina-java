package br.com.walter.designpatterns.api;

import br.com.walter.designpatterns.event.TransactionCompleted;
import br.com.walter.designpatterns.service.TransactionFacade;
import br.com.walter.designpatterns.service.TransactionHistory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class TransactionController {
    private final TransactionFacade transactionFacade;
    private final TransactionHistory transactionHistory;

    public TransactionController(
            TransactionFacade transactionFacade,
            TransactionHistory transactionHistory
    ) {
        this.transactionFacade = transactionFacade;
        this.transactionHistory = transactionHistory;
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable String accountId) {
        return transactionFacade.getAccount(accountId);
    }

    @GetMapping("/{accountId}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable String accountId) {
        transactionFacade.getAccount(accountId);
        return transactionHistory.findByAccountId(accountId).stream()
                .map(TransactionController::toResponse)
                .toList();
    }

    @PostMapping("/{accountId}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @PathVariable String accountId,
            @RequestBody TransactionRequest request
    ) {
        return transactionFacade.process(accountId, request);
    }

    private static TransactionResponse toResponse(TransactionCompleted event) {
        return new TransactionResponse(
                event.transactionId(),
                event.accountId(),
                event.type(),
                event.amount(),
                event.balanceAfter(),
                event.createdAt()
        );
    }
}
