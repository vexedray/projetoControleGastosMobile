package com.expense.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respostas de Expense
 */
public record ExpenseResponseDTO(
        Long id,
        BigDecimal value,
        LocalDateTime date,
        CategoryResponseDTO category,
        UserResponseDTO user
) {}