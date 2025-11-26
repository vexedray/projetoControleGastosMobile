package com.expense.controller;

import com.expense.assembler.ExpenseModelAssembler;
import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.mapper.ExpenseMapper;
import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.model.hateoas.ExpenseModel;
import com.expense.repository.UserRepository;
import com.expense.service.ExpenseService;
import com.expense.service.CategoryService;
import com.expense.service.UserService;
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
    public ResponseEntity<CollectionModel<ExpenseModel>> getAllExpenses() {
        try {
            Long userId = getCurrentUserId();
            logger.info("GET /api/expenses - Fetching all expenses for user: {}", userId);
            
            List<Expense> expenses = expenseService.getAllExpenses(userId);
            List<ExpenseResponseDTO> expensesDTO = expenses.stream()
                    .map(expenseMapper::toResponseDTO)
                    .collect(Collectors.toList());
            
            CollectionModel<ExpenseModel> expenseModels = CollectionModel.of(
                expensesDTO.stream()
                    .map(expenseModelAssembler::toModel)
                    .collect(Collectors.toList())
            );
            
            expenseModels.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withSelfRel());
            
            logger.info("Found {} expenses for user {}", expenses.size(), userId);
            return ResponseEntity.ok(expenseModels);
        } catch (Exception e) {
            logger.error("Error fetching expenses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseModel> getExpenseById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            logger.info("GET /api/expenses/{} - Fetching expense for user: {}", id, userId);
            
            return expenseService.getExpenseById(id, userId)
                    .map(expense -> {
                        logger.info("Expense found: amount={}", expense.getAmount());
                        ExpenseResponseDTO dto = expenseMapper.toResponseDTO(expense);
                        ExpenseModel model = expenseModelAssembler.toModel(dto);
                        return ResponseEntity.ok(model);
                    })
                    .orElseGet(() -> {
                        logger.warn("Expense {} not found for user {}", id, userId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching expense", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
        try {
            Long userId = getCurrentUserId();
            logger.info("POST /api/expenses - Creating expense for user: {}", userId);
            
            Expense expense = expenseService.createExpense(requestDTO, userId);
            ExpenseResponseDTO dto = expenseMapper.toResponseDTO(expense);
            ExpenseModel model = expenseModelAssembler.toModel(dto);
            
            logger.info("Expense created with ID: {}", expense.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        } catch (Exception e) {
            logger.error("Error creating expense", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseModel> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO requestDTO) {
        try {
            Long userId = getCurrentUserId();
            logger.info("PUT /api/expenses/{} - Updating expense for user: {}", id, userId);
            
            Expense expense = expenseService.updateExpense(id, requestDTO, userId);
            ExpenseResponseDTO dto = expenseMapper.toResponseDTO(expense);
            ExpenseModel model = expenseModelAssembler.toModel(dto);
            
            logger.info("Expense {} updated successfully", id);
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            logger.error("Expense not found or unauthorized", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating expense", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            logger.info("DELETE /api/expenses/{} - Deleting expense for user: {}", id, userId);
            
            expenseService.deleteExpense(id, userId);
            logger.info("Expense {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Expense not found or unauthorized", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting expense", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}