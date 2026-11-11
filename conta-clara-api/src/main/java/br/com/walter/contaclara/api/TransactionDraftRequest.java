package br.com.walter.contaclara.api;

import br.com.walter.contaclara.domain.TransactionCategory;
import br.com.walter.contaclara.domain.TransactionType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransactionDraftRequest(
        @NotBlank @Size(max = 120) String description,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal amount,
        @NotNull TransactionType type,
        @NotNull TransactionCategory category
) {}
