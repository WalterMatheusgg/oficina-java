package br.com.walter.contaclara.application;

public class FinanceNotFoundException extends RuntimeException {
    public FinanceNotFoundException(String message) {
        super(message);
    }
}
