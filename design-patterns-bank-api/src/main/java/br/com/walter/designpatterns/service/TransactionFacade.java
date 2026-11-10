package br.com.walter.designpatterns.service;

import br.com.walter.designpatterns.api.AccountResponse;
import br.com.walter.designpatterns.api.TransactionRequest;
import br.com.walter.designpatterns.api.TransactionResponse;
import br.com.walter.designpatterns.domain.Account;
import br.com.walter.designpatterns.repository.AccountRepository;
import br.com.walter.designpatterns.strategy.TransactionStrategy;
import br.com.walter.designpatterns.strategy.TransactionStrategyResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
public class TransactionFacade {
    private final AccountRepository accountRepository;
    private final TransactionStrategyResolver strategyResolver;

    public TransactionFacade(
            AccountRepository accountRepository,
            TransactionStrategyResolver strategyResolver
    ) {
        this.accountRepository = accountRepository;
        this.strategyResolver = strategyResolver;
    }

    public AccountResponse getAccount(String accountId) {
        Account account = findAccount(accountId);
        return new AccountResponse(account.id(), account.balance());
    }

    public TransactionResponse process(String accountId, TransactionRequest request) {
        if (request == null || request.type() == null || request.amount() == null) {
            throw new IllegalArgumentException("Transaction type and amount are required.");
        }

        BigDecimal amount = request.amount();
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        Account account = findAccount(accountId);
        TransactionStrategy strategy = strategyResolver.resolve(request.type());
        BigDecimal balanceAfter = strategy.execute(account, amount);

        return new TransactionResponse(account.id(), request.type(), amount, balanceAfter);
    }

    private Account findAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + accountId));
    }
}
