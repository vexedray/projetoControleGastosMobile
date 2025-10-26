package com.expense.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for category creation and update requests
 */
public class CategoryRequestDTO {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;
    
    private String color;
    
    private String icon;

    // Constructors
    public CategoryRequestDTO() {
    }

    public CategoryRequestDTO(String name, String description, String color, String icon) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
