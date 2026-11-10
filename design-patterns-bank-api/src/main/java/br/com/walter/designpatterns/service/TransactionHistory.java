package br.com.walter.designpatterns.service;

import br.com.walter.designpatterns.event.TransactionCompleted;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TransactionHistory {
    private final List<TransactionCompleted> entries = new CopyOnWriteArrayList<>();

    @EventListener
    public void record(TransactionCompleted event) {
        entries.add(event);
    }

    public List<TransactionCompleted> findByAccountId(String accountId) {
        return entries.stream()
                .filter(entry -> entry.accountId().equals(accountId))
                .sorted(Comparator.comparing(TransactionCompleted::createdAt).reversed())
                .toList();
    }
}
