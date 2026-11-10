package br.com.walter.designpatterns.strategy;

import br.com.walter.designpatterns.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class TransactionStrategyResolver {
    private final Map<TransactionType, TransactionStrategy> strategies;

    public TransactionStrategyResolver(List<TransactionStrategy> strategies) {
        Map<TransactionType, TransactionStrategy> byType = new EnumMap<>(TransactionType.class);
        for (TransactionStrategy strategy : strategies) {
            TransactionStrategy previous = byType.put(strategy.type(), strategy);
            if (previous != null) {
                throw new IllegalStateException("More than one strategy registered for " + strategy.type());
            }
        }
        this.strategies = Map.copyOf(byType);
    }

    public TransactionStrategy resolve(TransactionType type) {
        TransactionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }
        return strategy;
    }
}
