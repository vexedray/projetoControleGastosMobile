package com.expense.dto.response;

import java.time.LocalDateTime;

/**
 * DTO para respostas de Category
 */
public record CategoryResponseDTO(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt
) {}