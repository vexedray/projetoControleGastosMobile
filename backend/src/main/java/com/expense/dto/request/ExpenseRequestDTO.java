package com.expense.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para requisições de Expense
 */
public record ExpenseRequestDTO(
        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser positivo")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal value,
        
        LocalDateTime date,
        
        @NotNull(message = "ID da categoria é obrigatório")
        Long categoryId,
        
        @NotNull(message = "ID do usuário é obrigatório")
        Long userId
) {}