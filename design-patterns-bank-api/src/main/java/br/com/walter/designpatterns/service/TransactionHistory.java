package br.com.walter.designpatterns.service;

import br.com.walter.designpatterns.api.TransactionResponse;
import br.com.walter.designpatterns.event.TransactionCompleted;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TransactionHistory {
    private final List<TransactionResponse> entries = new CopyOnWriteArrayList<>();

    @EventListener
    public void record(TransactionCompleted event) {
        entries.add(event.transaction());
    }

    public List<TransactionResponse> findByAccountId(String accountId) {
        return entries.stream()
                .filter(entry -> entry.accountId().equals(accountId))
                .sorted(Comparator.comparing(TransactionResponse::createdAt).reversed())
                .toList();
    }
}
