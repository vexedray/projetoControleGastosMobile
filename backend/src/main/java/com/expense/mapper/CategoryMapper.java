package com.expense.mapper;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.model.Category;

/**
 * Mapper para conversões entre Category e seus DTOs
 */
public class CategoryMapper {

    /**
     * Converte entidade Category para CategoryResponseDTO
     */
    public static CategoryResponseDTO toDTO(Category entity) {
        if (entity == null) {
            return null;
        }
        
        return new CategoryResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }

    /**
     * Converte CategoryRequestDTO para entidade Category
     */
    public static Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Category category = new Category();
        category.setName(dto.name());
        category.setDescription(dto.description());
        return category;
    }

    /**
     * Atualiza entidade Category existente com dados do CategoryRequestDTO
     */
    public static void updateEntity(Category entity, CategoryRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setName(dto.name());
        entity.setDescription(dto.description());
    }
}