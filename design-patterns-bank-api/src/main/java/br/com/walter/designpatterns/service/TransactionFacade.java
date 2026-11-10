package br.com.walter.designpatterns.service;

import br.com.walter.designpatterns.api.AccountResponse;
import br.com.walter.designpatterns.api.TransactionRequest;
import br.com.walter.designpatterns.api.TransactionResponse;
import br.com.walter.designpatterns.domain.Account;
import br.com.walter.designpatterns.domain.TransactionRecord;
import br.com.walter.designpatterns.event.TransactionCompleted;
import br.com.walter.designpatterns.repository.AccountRepository;
import br.com.walter.designpatterns.repository.TransactionRepository;
import br.com.walter.designpatterns.strategy.TransactionStrategy;
import br.com.walter.designpatterns.strategy.TransactionStrategyResolver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TransactionFacade {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionStrategyResolver strategyResolver;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionFacade(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            TransactionStrategyResolver strategyResolver,
            ApplicationEventPublisher eventPublisher
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.strategyResolver = strategyResolver;
        this.eventPublisher = eventPublisher;
    }

    public AccountResponse getAccount(String accountId) {
        Account account = findAccount(accountId);
        return new AccountResponse(account.id(), account.balance());
    }

    @Transactional
    public TransactionResponse process(String accountId, String idempotencyKey, TransactionRequest request) {
        if (request == null || request.type() == null || request.amount() == null) {
            throw new IllegalArgumentException("Transaction type and amount are required.");
        }
        validateIdempotencyKey(idempotencyKey);

        BigDecimal amount = request.amount();
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        try {
            amount = amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Amount cannot have more than two decimal places.");
        }

        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + accountId));

        var existing = transactionRepository.findByAccountIdAndIdempotencyKey(accountId, idempotencyKey);
        if (existing.isPresent()) {
            TransactionRecord previous = existing.get();
            if (previous.type() != request.type() || previous.amount().compareTo(amount) != 0) {
                throw new IdempotencyConflictException(
                        "This Idempotency-Key was already used with a different transaction."
                );
            }
            return toResponse(previous);
        }

        TransactionStrategy strategy = strategyResolver.resolve(request.type());
        BigDecimal balanceAfter = strategy.execute(account, amount);
        UUID transactionId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        accountRepository.save(account);
        TransactionRecord transaction = new TransactionRecord(
                transactionId,
                account.id(),
                idempotencyKey,
                request.type(),
                amount,
                balanceAfter,
                createdAt
        );
        transactionRepository.save(transaction);

        eventPublisher.publishEvent(new TransactionCompleted(
                transactionId,
                account.id(),
                request.type(),
                amount,
                balanceAfter,
                createdAt
        ));

        return toResponse(transaction);
    }

    private void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key header is required.");
        }
        if (idempotencyKey.length() > 128) {
            throw new IllegalArgumentException("Idempotency-Key must contain at most 128 characters.");
        }
    }

    private TransactionResponse toResponse(TransactionRecord transaction) {
        return new TransactionResponse(
                transaction.transactionId(),
                transaction.accountId(),
                transaction.type(),
                transaction.amount(),
                transaction.balanceAfter(),
                transaction.createdAt()
        );
    }

    private Account findAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + accountId));
    }
}
