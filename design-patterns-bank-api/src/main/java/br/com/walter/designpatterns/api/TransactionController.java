package br.com.walter.designpatterns.api;

import br.com.walter.designpatterns.service.TransactionFacade;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class TransactionController {
    private final TransactionFacade transactionFacade;

    public TransactionController(TransactionFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable String accountId) {
        return transactionFacade.getAccount(accountId);
    }

    @PostMapping("/{accountId}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @PathVariable String accountId,
            @RequestBody TransactionRequest request
    ) {
        return transactionFacade.process(accountId, request);
    }
}
