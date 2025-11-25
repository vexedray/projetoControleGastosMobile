package com.expense.controller;

import com.expense.assembler.ExpenseModelAssembler;
import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.mapper.ExpenseMapper;
import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.model.hateoas.ExpenseModel;
import com.expense.service.ExpenseService;
import com.expense.service.CategoryService;
import com.expense.service.UserService;
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
@RequestMapping("/api/expenses")
public class ExpenseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseMapper expenseMapper;
    
    @Autowired
    private ExpenseModelAssembler expenseModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<ExpenseModel>> getAllExpenses() {
        logger.info("GET /api/expenses - Fetching all expenses");
        List<Expense> expenses = expenseService.findAll();
        List<ExpenseResponseDTO> expensesDTO = expenses.stream()
                .map(expenseMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        // Converte para HATEOAS models
        CollectionModel<ExpenseModel> expenseModels = CollectionModel.of(
            expensesDTO.stream()
                .map(expenseModelAssembler::toModel)
                .collect(Collectors.toList())
        );
        
        // Adiciona link para a própria coleção
        expenseModels.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withSelfRel());
        
        logger.info("Found {} expenses", expenses.size());
        return ResponseEntity.ok(expenseModels);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseModel> getExpenseById(@PathVariable Long id) {
        logger.info("GET /api/expenses/{} - Fetching expense by ID", id);
        return expenseService.findById(id)
                .map(expense -> {
                    logger.info("Expense found: amount={}", expense.getAmount());
                    ExpenseResponseDTO dto = expenseMapper.toResponseDTO(expense);
                    ExpenseModel model = expenseModelAssembler.toModel(dto);
                    return ResponseEntity.ok(model);
                })
                .orElseGet(() -> {
                    logger.warn("Expense with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<ExpenseModel>> getExpensesByUser(@PathVariable Long userId) {
        logger.info("GET /api/expenses/user/{} - Fetching user expenses", userId);
        
        return userService.getUserById(userId)
                .map(user -> {
                    List<Expense> expenses = expenseService.findByUserId(userId);
                    List<ExpenseResponseDTO> expensesDTO = expenses.stream()
                            .map(expenseMapper::toResponseDTO)
                            .collect(Collectors.toList());
                    
                    // Converte para HATEOAS models
                    CollectionModel<ExpenseModel> expenseModels = CollectionModel.of(
                        expensesDTO.stream()
                            .map(expenseModelAssembler::toModel)
                            .collect(Collectors.toList())
                    );
                    
                    // Adiciona links
                    expenseModels.add(linkTo(methodOn(ExpenseController.class).getExpensesByUser(userId)).withSelfRel());
                    expenseModels.add(linkTo(methodOn(UserController.class).getUserById(userId)).withRel("user"));
                    expenseModels.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("all-expenses"));
                    
                    logger.info("Found {} expenses for user {}", expenses.size(), userId);
                    return ResponseEntity.ok(expenseModels);
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found", userId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CollectionModel<ExpenseModel>> getExpensesByCategory(@PathVariable Long categoryId) {
        logger.info("GET /api/expenses/category/{} - Fetching category expenses", categoryId);
        
        return categoryService.findById(categoryId)
                .map(category -> {
                    List<Expense> expenses = expenseService.findByCategoryId(categoryId);
                    List<ExpenseResponseDTO> expensesDTO = expenses.stream()
                            .map(expenseMapper::toResponseDTO)
                            .collect(Collectors.toList());
                    
                    // Converte para HATEOAS models
                    CollectionModel<ExpenseModel> expenseModels = CollectionModel.of(
                        expensesDTO.stream()
                            .map(expenseModelAssembler::toModel)
                            .collect(Collectors.toList())
                    );
                    
                    // Adiciona links
                    expenseModels.add(linkTo(methodOn(ExpenseController.class).getExpensesByCategory(categoryId)).withSelfRel());
                    expenseModels.add(linkTo(methodOn(CategoryController.class).getCategoryById(categoryId)).withRel("category"));
                    expenseModels.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("all-expenses"));
                    
                    logger.info("Found {} expenses for category {}", expenses.size(), categoryId);
                    return ResponseEntity.ok(expenseModels);
                })
                .orElseGet(() -> {
                    logger.warn("Category with ID {} not found", categoryId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<ExpenseModel> createExpense(@Valid @RequestBody ExpenseRequestDTO requestDTO) {
        logger.info("POST /api/expenses - Creating new expense");
        
        try {
            // Buscar categoria
            Category category = categoryService.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Category with ID {} not found", requestDTO.getCategoryId());
                        return new RuntimeException("Category not found");
                    });
            
            // Buscar usuário - se userId não foi fornecido, usa o do requestDTO
            Long userId = requestDTO.getUserId();
            if (userId == null) {
                logger.warn("UserId not provided in request, this should not happen");
                throw new RuntimeException("User ID is required");
            }
            
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> {
                        logger.error("User with ID {} not found", userId);
                        return new RuntimeException("User not found");
                    });

            Expense expense = expenseMapper.toEntity(requestDTO);
            expense.setCategory(category);
            expense.setUser(user);
            
            Expense savedExpense = expenseService.save(expense);
            ExpenseResponseDTO dto = expenseMapper.toResponseDTO(savedExpense);
            ExpenseModel model = expenseModelAssembler.toModel(dto);
            
            logger.info("Expense created with ID: {} - amount: {} - category: {} - user: {}", 
                savedExpense.getId(), 
                requestDTO.getAmount(), 
                category.getName(),
                user.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
            
        } catch (RuntimeException e) {
            logger.error("Error creating expense: {}", e.getMessage());
            throw e;
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseModel> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO requestDTO) {
        logger.info("PUT /api/expenses/{} - Updating expense", id);
        
        try {
            return expenseService.findById(id)
                    .map(existingExpense -> {
                        existingExpense.setAmount(requestDTO.getAmount());
                        existingExpense.setDescription(requestDTO.getDescription());
                        
                        if (requestDTO.getCategoryId() != null) {
                            Category category = categoryService.findById(requestDTO.getCategoryId())
                                    .orElseThrow(() -> new RuntimeException("Category not found"));
                            existingExpense.setCategory(category);
                        }
                        
                        Expense updated = expenseService.save(existingExpense);
                        ExpenseResponseDTO dto = expenseMapper.toResponseDTO(updated);
                        ExpenseModel model = expenseModelAssembler.toModel(dto);
                        logger.info("Expense {} updated successfully", id);
                        return ResponseEntity.ok(model);
                    })
                    .orElseGet(() -> {
                        logger.warn("Expense with ID {} not found for update", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error updating expense: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        logger.info("DELETE /api/expenses/{} - Deleting expense", id);
        
        return expenseService.findById(id)
                .map(expense -> {
                    expenseService.deleteById(id);
                    logger.info("Expense {} deleted successfully", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Expense with ID {} not found for deletion", id);
                    return ResponseEntity.notFound().build();
                });
    }
}