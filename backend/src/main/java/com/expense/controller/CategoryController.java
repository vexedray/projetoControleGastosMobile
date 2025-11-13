package com.expense.controller;

import com.expense.model.Category;
import com.expense.service.CategoryService;
import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.mapper.CategoryMapper;
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
    private CategoryService categoryService;    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        logger.info("GET /api/categories - Buscando todas as categorias");
        List<Category> categories = categoryService.findAll();
        List<CategoryResponseDTO> categoryDTOs = categories.stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Encontradas {} categorias", categoryDTOs.size());
        return ResponseEntity.ok(categoryDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        logger.info("GET /api/categories/{} - Buscando categoria por ID", id);
        return categoryService.findById(id)
                .map(category -> {
                    CategoryResponseDTO categoryDTO = CategoryMapper.toDTO(category);
                    logger.info("Categoria encontrada: {}", category.getName());
                    return ResponseEntity.ok(categoryDTO);
                })
                .orElseGet(() -> {
                    logger.warn("Categoria com ID {} não encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("POST /api/categories - Criando categoria: {}", categoryRequestDTO.name());
        
        Category category = CategoryMapper.toEntity(categoryRequestDTO);
        Category savedCategory = categoryService.save(category);
        CategoryResponseDTO responseDTO = CategoryMapper.toDTO(savedCategory);
        
        logger.info("Categoria criada com ID: {}", savedCategory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("PUT /api/categories/{} - Atualizando categoria", id);
        
        return categoryService.findById(id)
                .map(existingCategory -> {
                    CategoryMapper.updateEntity(existingCategory, categoryRequestDTO);
                    Category updated = categoryService.save(existingCategory);
                    CategoryResponseDTO responseDTO = CategoryMapper.toDTO(updated);
                    
                    logger.info("Categoria {} atualizada com sucesso", id);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElseGet(() -> {
                    logger.warn("Categoria com ID {} não encontrada para atualização", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("DELETE /api/categories/{} - Deletando categoria", id);
        
        return categoryService.findById(id)
                .map(category -> {
                    categoryService.deleteById(id);
                    logger.info("Categoria {} deletada com sucesso", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Categoria com ID {} não encontrada para deleção", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        logger.info("GET /api/categories/test - Endpoint de teste");
        int count = categoryService.findAll().size();
        return ResponseEntity.ok("API funcionando! Número de categorias: " + count);
    }
}