package br.com.walter.designpatterns.repository;

import br.com.walter.designpatterns.domain.Account;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public InMemoryAccountRepository() {
        accounts.put("ACC-1001", new Account("ACC-1001", new BigDecimal("1000.00")));
        accounts.put("ACC-1002", new Account("ACC-1002", new BigDecimal("250.00")));
    }

    @Override
    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
}
