package br.com.walter.designpatterns.persistence;

import br.com.walter.designpatterns.domain.Account;
import br.com.walter.designpatterns.repository.AccountRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class JpaAccountRepository implements AccountRepository {
    private final AccountJpaRepository accounts;

    public JpaAccountRepository(AccountJpaRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(String accountId) {
        return accounts.findById(accountId).map(AccountEntity::toDomain);
    }

    @Override
    @Transactional
    public Optional<Account> findByIdForUpdate(String accountId) {
        return accounts.findByIdForUpdate(accountId).map(AccountEntity::toDomain);
    }

    @Override
    @Transactional
    public Account save(Account account) {
        AccountEntity entity = accounts.findById(account.id())
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + account.id()));
        entity.updateBalance(account.balance());
        return accounts.save(entity).toDomain();
    }
}
