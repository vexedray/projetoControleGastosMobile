package com.expense.controller;

import com.expense.assembler.CategoryModelAssembler;
import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.mapper.CategoryMapper;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.model.hateoas.CategoryModel;
import com.expense.repository.UserRepository;
import com.expense.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get current authenticated user ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }
    
    @GetMapping
    public ResponseEntity<CollectionModel<CategoryModel>> getAllCategories() {
        try {
            Long userId = getCurrentUserId();
            logger.info("GET /api/categories - Fetching all categories for user: {}", userId);
            
            List<Category> categories = categoryService.getAllCategories(userId);
            List<CategoryResponseDTO> categoriesDTO = categories.stream()
                    .map(categoryMapper::toResponseDTO)
                    .collect(Collectors.toList());
            
            CollectionModel<CategoryModel> categoryModels = CollectionModel.of(
                categoriesDTO.stream()
                    .map(categoryModelAssembler::toModel)
                    .collect(Collectors.toList())
            );
            
            categoryModels.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel());
            
            logger.info("Found {} categories for user {}", categories.size(), userId);
            return ResponseEntity.ok(categoryModels);
        } catch (Exception e) {
            logger.error("Error fetching categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryModel> getCategoryById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            logger.info("GET /api/categories/{} - Fetching category for user: {}", id, userId);
            
            return categoryService.getCategoryById(id, userId)
                    .map(category -> {
                        logger.info("Category found: {}", category.getName());
                        CategoryResponseDTO dto = categoryMapper.toResponseDTO(category);
                        CategoryModel model = categoryModelAssembler.toModel(dto);
                        return ResponseEntity.ok(model);
                    })
                    .orElseGet(() -> {
                        logger.warn("Category {} not found for user {}", id, userId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<CategoryModel> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        try {
            Long userId = getCurrentUserId();
            logger.info("POST /api/categories - Creating category for user: {}", userId);
            logger.info("Category data: {}", requestDTO.getName());
            
            Category category = categoryService.createCategory(requestDTO, userId);
            CategoryResponseDTO dto = categoryMapper.toResponseDTO(category);
            CategoryModel model = categoryModelAssembler.toModel(dto);
            
            logger.info("Category created with ID: {}", category.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        } catch (Exception e) {
            logger.error("Error creating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryModel> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        try {
            Long userId = getCurrentUserId();
            logger.info("PUT /api/categories/{} - Updating category for user: {}", id, userId);
            
            Category category = categoryService.updateCategory(id, requestDTO, userId);
            CategoryResponseDTO dto = categoryMapper.toResponseDTO(category);
            CategoryModel model = categoryModelAssembler.toModel(dto);
            
            logger.info("Category {} updated successfully", id);
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            logger.error("Category not found or unauthorized", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            logger.info("DELETE /api/categories/{} - Deleting category for user: {}", id, userId);
            
            categoryService.deleteCategory(id, userId);
            logger.info("Category {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Category not found or unauthorized", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        try {
            Long userId = getCurrentUserId();
            logger.info("GET /api/categories/test - Test endpoint for user: {}", userId);
            int count = categoryService.getAllCategories(userId).size();
            return ResponseEntity.ok("API working! Number of categories for user " + userId + ": " + count);
        } catch (Exception e) {
            logger.error("Error in test endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}