package com.expense.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for expense response data
 */
public class ExpenseResponseDTO {
    
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private CategoryResponseDTO category;
    private UserResponseDTO user;

    // Constructors
    public ExpenseResponseDTO() {
    }

    public ExpenseResponseDTO(Long id, String description, BigDecimal amount, LocalDate date, 
                             CategoryResponseDTO category, UserResponseDTO user) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public CategoryResponseDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryResponseDTO category) {
        this.category = category;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }
}
