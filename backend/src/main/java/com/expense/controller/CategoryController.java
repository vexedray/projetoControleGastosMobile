package com.expense.controller;

import com.expense.model.Category;
import com.expense.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public List<Category> getAllCategories() {
        logger.info("GET /api/categories - Buscando todas as categorias");
        List<Category> categories = categoryService.findAll();
        logger.info("Encontradas {} categorias", categories.size());
        return categories;
    }
    
    @GetMapping("/test")
    public String testEndpoint() {
        logger.info("GET /api/categories/test - Endpoint de teste");
        return "API funcionando! Número de categorias: " + categoryService.findAll().size();
    }
    
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        logger.info("POST /api/categories - Criando categoria: {}", category.getNome());
        Category savedCategory = categoryService.save(category);
        logger.info("Categoria criada com ID: {}", savedCategory.getId());
        return savedCategory;
    }
}