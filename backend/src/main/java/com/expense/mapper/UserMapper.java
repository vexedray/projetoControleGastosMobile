package com.expense.mapper;

import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.model.User;

/**
 * Mapper para conversões entre User e seus DTOs
 */
public class UserMapper {

    /**
     * Converte entidade User para UserResponseDTO
     * Nota: Senha não é incluída por segurança
     */
    public static UserResponseDTO toDTO(User entity) {
        if (entity == null) {
            return null;
        }
        
        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getCreatedAt()
        );
    }

    /**
     * Converte UserRequestDTO para entidade User
     */
    public static User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        return user;
    }

    /**
     * Atualiza entidade User existente com dados do UserRequestDTO
     */
    public static void updateEntity(User entity, UserRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        // Só atualiza a senha se foi fornecida
        if (dto.password() != null && !dto.password().trim().isEmpty()) {
            entity.setPassword(dto.password());
        }
    }

    /**
     * Atualiza entidade User existente com dados do UserRequestDTO (incluindo senha obrigatória)
     */
    public static void updateEntityWithPassword(User entity, UserRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
    }
}