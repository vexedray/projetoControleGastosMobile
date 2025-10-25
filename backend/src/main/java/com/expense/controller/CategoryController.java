package com.expense.controller;

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

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.info("GET /api/categories - Buscando todas as categorias");
        List<Category> categories = categoryService.findAll();
        logger.info("Encontradas {} categorias", categories.size());
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        logger.info("GET /api/categories/{} - Buscando categoria por ID", id);
        return categoryService.findById(id)
                .map(category -> {
                    logger.info("Categoria encontrada: {}", category.getName());
                    return ResponseEntity.ok(category);
                })
                .orElseGet(() -> {
                    logger.warn("Categoria com ID {} não encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        logger.info("POST /api/categories - Criando categoria: {}", category.getName());
        Category savedCategory = categoryService.save(category);
        logger.info("Categoria criada com ID: {}", savedCategory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody Category category) {
        logger.info("PUT /api/categories/{} - Atualizando categoria", id);
        
        return categoryService.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(category.getName());
                    existingCategory.setDescription(category.getDescription());
                    Category updated = categoryService.save(existingCategory);
                    logger.info("Categoria {} atualizada com sucesso", id);
                    return ResponseEntity.ok(updated);
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