package com.expense.dto.response;

import java.time.LocalDateTime;

/**
 * DTO para respostas de User
 * Nota: Senha não é incluída por segurança
 */
public record UserResponseDTO(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt
) {}