package br.com.walter.designpatterns.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionCompletedListener {
    private static final Logger log = LoggerFactory.getLogger(TransactionCompletedListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(TransactionCompleted event) {
        log.info("Transaction {} committed for account {}", event.transactionId(), event.accountId());
    }
}
