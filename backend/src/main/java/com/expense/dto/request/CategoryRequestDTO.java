package com.expense.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisições de Category
 */
public record CategoryRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String name,
        
        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String description
) {}