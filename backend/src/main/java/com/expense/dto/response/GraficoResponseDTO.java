package com.expense.dto.response;

import java.math.BigDecimal;

public class GraficoResponseDTO {
    
    private String categoryName;
    private BigDecimal totalValue;
    private Long count;
    private Double percentage;
    
    // Construtores
    public GraficoResponseDTO() {
    }
    
    public GraficoResponseDTO(String categoryName, BigDecimal totalValue, Long count) {
        this.categoryName = categoryName;
        this.totalValue = totalValue;
        this.count = count;
    }
    
    public GraficoResponseDTO(String categoryName, BigDecimal totalValue, Long count, Double percentage) {
        this(categoryName, totalValue, count);
        this.percentage = percentage;
    }
    
    // Getters e Setters
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public BigDecimal getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    
    public Long getCount() {
        return count;
    }
    
    public void setCount(Long count) {
        this.count = count;
    }
    
    public Double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
