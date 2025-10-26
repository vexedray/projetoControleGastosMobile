package com.expense.dto.response;

/**
 * DTO for category response data
 */
public class CategoryResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private String color;
    private String icon;

    // Constructors
    public CategoryResponseDTO() {
    }

    public CategoryResponseDTO(Long id, String name, String description, String color, String icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
