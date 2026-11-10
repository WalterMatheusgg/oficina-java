package br.com.walter.designpatterns.repository;

import br.com.walter.designpatterns.domain.Account;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(String accountId);
}
