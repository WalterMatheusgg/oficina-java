package br.com.walter.designpatterns.service;

import br.com.walter.designpatterns.domain.TransactionRecord;
import br.com.walter.designpatterns.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionHistory {
    private final TransactionRepository transactionRepository;

    public TransactionHistory(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionRecord> findByAccountId(String accountId) {
        return transactionRepository.findAllByAccountId(accountId);
    }
}
