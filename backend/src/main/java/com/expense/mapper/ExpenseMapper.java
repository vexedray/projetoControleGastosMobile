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
        expense.setValue(dto.getAmount()); // DTO uses 'amount', model uses 'value'
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
        // Use empty string for description since model doesn't have it
        dto.setDescription("");
        dto.setAmount(expense.getValue()); // Model uses 'value', DTO uses 'amount'
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
        
        if (dto.getAmount() != null) {
            expense.setValue(dto.getAmount());
        }
        if (dto.getDate() != null) {
            expense.setDate(dto.getDate().atStartOfDay());
        }
    }
}
