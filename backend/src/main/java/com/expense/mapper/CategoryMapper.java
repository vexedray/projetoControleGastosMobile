package com.expense.mapper;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    
    /**
     * Converts CategoryRequestDTO to Category entity
     */
    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        category.setIcon(dto.getIcon());
        return category;
    }
    
    /**
     * Converts Category entity to CategoryResponseDTO
     */
    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setColor(category.getColor());
        dto.setIcon(category.getIcon());
        return dto;
    }
    
    /**
     * Updates an existing Category entity with DTO data
     */
    public void updateEntity(Category category, CategoryRequestDTO dto) {
        if (category == null || dto == null) {
            return;
        }
        
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
        if (dto.getColor() != null) {
            category.setColor(dto.getColor());
        }
        if (dto.getIcon() != null) {
            category.setIcon(dto.getIcon());
        }
    }
}
