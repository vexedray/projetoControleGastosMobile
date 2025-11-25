package com.expense.controller;

import com.expense.assembler.CategoryModelAssembler;
import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.mapper.CategoryMapper;
import com.expense.model.Category;
import com.expense.model.hateoas.CategoryModel;
import com.expense.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private CategoryModelAssembler categoryModelAssembler;
    
    @GetMapping
    public ResponseEntity<CollectionModel<CategoryModel>> getAllCategories() {
        logger.info("GET /api/categories - Fetching all categories");
        List<Category> categories = categoryService.findAll();
        List<CategoryResponseDTO> categoriesDTO = categories.stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        // Converte para HATEOAS models
        CollectionModel<CategoryModel> categoryModels = CollectionModel.of(
            categoriesDTO.stream()
                .map(categoryModelAssembler::toModel)
                .collect(Collectors.toList())
        );
        
        // Adiciona link para a própria coleção
        categoryModels.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
        
        logger.info("Found {} categories", categories.size());
        return ResponseEntity.ok(categoryModels);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryModel> getCategoryById(@PathVariable Long id) {
        logger.info("GET /api/categories/{} - Fetching category by ID", id);
        return categoryService.findById(id)
                .map(category -> {
                    logger.info("Category found: {}", category.getName());
                    CategoryResponseDTO dto = categoryMapper.toResponseDTO(category);
                    CategoryModel model = categoryModelAssembler.toModel(dto);
                    return ResponseEntity.ok(model);
                })
                .orElseGet(() -> {
                    logger.warn("Category with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<CategoryModel> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        logger.info("POST /api/categories - Creating category: {}", requestDTO.getName());
        Category category = categoryMapper.toEntity(requestDTO);
        Category savedCategory = categoryService.save(category);
        CategoryResponseDTO dto = categoryMapper.toResponseDTO(savedCategory);
        CategoryModel model = categoryModelAssembler.toModel(dto);
        logger.info("Category created with ID: {}", savedCategory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryModel> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        logger.info("PUT /api/categories/{} - Updating category", id);
        
        return categoryService.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(requestDTO.getName());
                    existingCategory.setDescription(requestDTO.getDescription());
                    existingCategory.setColor(requestDTO.getColor());
                    existingCategory.setIcon(requestDTO.getIcon());
                    Category updated = categoryService.save(existingCategory);
                    CategoryResponseDTO dto = categoryMapper.toResponseDTO(updated);
                    CategoryModel model = categoryModelAssembler.toModel(dto);
                    logger.info("Category {} updated successfully", id);
                    return ResponseEntity.ok(model);
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