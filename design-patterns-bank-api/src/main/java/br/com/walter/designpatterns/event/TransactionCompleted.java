package br.com.walter.designpatterns.event;

import br.com.walter.designpatterns.api.TransactionResponse;

/**
 * Evento publicado depois que uma operação altera o saldo da conta.
 */
public record TransactionCompleted(TransactionResponse transaction) {
}
