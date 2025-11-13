package com.expense.mapper;

import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;

import java.time.LocalDateTime;

/**
 * Mapper para conversões entre Expense e seus DTOs
 */
public class ExpenseMapper {

    /**
     * Converte entidade Expense para ExpenseResponseDTO
     */
    public static ExpenseResponseDTO toDTO(Expense entity) {
        if (entity == null) {
            return null;
        }
        
        return new ExpenseResponseDTO(
                entity.getId(),
                entity.getValue(),
                entity.getDate(),
                CategoryMapper.toDTO(entity.getCategory()),
                UserMapper.toDTO(entity.getUser())
        );
    }

    /**
     * Converte ExpenseRequestDTO para entidade Expense
     * Nota: Category e User devem ser definidos separadamente
     */
    public static Expense toEntity(ExpenseRequestDTO dto, Category category, User user) {
        if (dto == null) {
            return null;
        }
        
        Expense expense = new Expense();
        expense.setValue(dto.value());
        expense.setDate(dto.date() != null ? dto.date() : LocalDateTime.now());
        expense.setCategory(category);
        expense.setUser(user);
        return expense;
    }

    /**
     * Atualiza entidade Expense existente com dados do ExpenseRequestDTO
     * Nota: Category e User devem ser definidos separadamente se necessário
     */
    public static void updateEntity(Expense entity, ExpenseRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setValue(dto.value());
        if (dto.date() != null) {
            entity.setDate(dto.date());
        }
        // Note: Category e User são atualizados separadamente se necessário
    }

    /**
     * Atualiza entidade Expense existente com dados do ExpenseRequestDTO incluindo relacionamentos
     */
    public static void updateEntityWithRelations(Expense entity, ExpenseRequestDTO dto, 
                                                Category category, User user) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setValue(dto.value());
        if (dto.date() != null) {
            entity.setDate(dto.date());
        }
        if (category != null) {
            entity.setCategory(category);
        }
        if (user != null) {
            entity.setUser(user);
        }
    }
}