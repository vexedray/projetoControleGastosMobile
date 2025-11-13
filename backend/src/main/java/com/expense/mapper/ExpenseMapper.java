package com.expense.mapper;

import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * Converts ExpenseRequestDTO to Expense entity (without relationships)
     * Relationships must be set in the Service layer
     */
    public Expense toEntity(ExpenseRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Expense expense = new Expense();
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        // Convert LocalDate to LocalDateTime
        if (dto.getDate() != null) {
            expense.setDate(dto.getDate().atStartOfDay());
        }
        return expense;
    }
    
    /**
     * Converts Expense entity to ExpenseResponseDTO
     */
    public ExpenseResponseDTO toResponseDTO(Expense expense) {
        if (expense == null) {
            return null;
        }
        
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        // Convert LocalDateTime to LocalDate
        if (expense.getDate() != null) {
            dto.setDate(expense.getDate().toLocalDate());
        }
        dto.setCategory(categoryMapper.toResponseDTO(expense.getCategory()));
        dto.setUser(userMapper.toResponseDTO(expense.getUser()));
        return dto;
    }
    
    /**
     * Updates an existing Expense entity with DTO data
     */
    public void updateEntity(Expense expense, ExpenseRequestDTO dto) {
        if (expense == null || dto == null) {
            return;
        }
        
        if (dto.getDescription() != null) {
            expense.setDescription(dto.getDescription());
        }
        if (dto.getAmount() != null) {
            expense.setAmount(dto.getAmount());
        }
        if (dto.getDate() != null) {
            expense.setDate(dto.getDate().atStartOfDay());
        }
    }
}
