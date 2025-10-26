package com.expense.controller;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.mapper.CategoryMapper;
import com.expense.model.Category;
import com.expense.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        logger.info("GET /api/categories - Fetching all categories");
        List<Category> categories = categoryService.findAll();
        List<CategoryResponseDTO> response = categories.stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.info("Found {} categories", categories.size());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        logger.info("GET /api/categories/{} - Fetching category by ID", id);
        return categoryService.findById(id)
                .map(category -> {
                    logger.info("Category found: {}", category.getName());
                    return ResponseEntity.ok(categoryMapper.toResponseDTO(category));
                })
                .orElseGet(() -> {
                    logger.warn("Category with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        logger.info("POST /api/categories - Creating category: {}", requestDTO.getName());
        Category category = categoryMapper.toEntity(requestDTO);
        Category savedCategory = categoryService.save(category);
        logger.info("Category created with ID: {}", savedCategory.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryMapper.toResponseDTO(savedCategory));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        logger.info("PUT /api/categories/{} - Updating category", id);
        
        return categoryService.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(requestDTO.getName());
                    existingCategory.setDescription(requestDTO.getDescription());
                    Category updated = categoryService.save(existingCategory);
                    logger.info("Category {} updated successfully", id);
                    return ResponseEntity.ok(categoryMapper.toResponseDTO(updated));
                })
                .orElseGet(() -> {
                    logger.warn("Category with ID {} not found for update", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("DELETE /api/categories/{} - Deleting category", id);
        
        return categoryService.findById(id)
                .map(category -> {
                    categoryService.deleteById(id);
                    logger.info("Category {} deleted successfully", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Category with ID {} not found for deletion", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        logger.info("GET /api/categories/test - Test endpoint");
        int count = categoryService.findAll().size();
        return ResponseEntity.ok("API working! Number of categories: " + count);
    }
}