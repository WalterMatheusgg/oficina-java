package br.com.walter.designpatterns.service;

import br.com.walter.designpatterns.api.AccountResponse;
import br.com.walter.designpatterns.api.TransactionRequest;
import br.com.walter.designpatterns.api.TransactionResponse;
import br.com.walter.designpatterns.domain.Account;
import br.com.walter.designpatterns.event.TransactionCompleted;
import br.com.walter.designpatterns.repository.AccountRepository;
import br.com.walter.designpatterns.strategy.TransactionStrategy;
import br.com.walter.designpatterns.strategy.TransactionStrategyResolver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TransactionFacade {
    private final AccountRepository accountRepository;
    private final TransactionStrategyResolver strategyResolver;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionFacade(
            AccountRepository accountRepository,
            TransactionStrategyResolver strategyResolver,
            ApplicationEventPublisher eventPublisher
    ) {
        this.accountRepository = accountRepository;
        this.strategyResolver = strategyResolver;
        this.eventPublisher = eventPublisher;
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

        TransactionResponse transaction = new TransactionResponse(
                UUID.randomUUID(),
                account.id(),
                request.type(),
                amount,
                balanceAfter,
                Instant.now()
        );

        eventPublisher.publishEvent(new TransactionCompleted(transaction));
        return transaction;
    }

    private Account findAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + accountId));
    }
}
