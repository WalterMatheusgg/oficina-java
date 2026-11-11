package br.com.walter.contaclara.api;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MonthlyBudgetRequest(
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal limit
) {}
