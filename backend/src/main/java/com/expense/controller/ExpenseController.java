package com.expense.controller;

import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.service.ExpenseService;
import com.expense.service.CategoryService;
import com.expense.service.UserService;
import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.mapper.ExpenseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses() {
        logger.info("GET /api/expenses - Buscando todas as despesas");
        List<Expense> expenses = expenseService.findAll();
        List<ExpenseResponseDTO> expenseDTOs = expenses.stream()
                .map(ExpenseMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Encontradas {} despesas", expenseDTOs.size());
        return ResponseEntity.ok(expenseDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id) {
        logger.info("GET /api/expenses/{} - Buscando despesa por ID", id);
        return expenseService.findById(id)
                .map(expense -> {
                    ExpenseResponseDTO expenseDTO = ExpenseMapper.toDTO(expense);
                    logger.info("Despesa encontrada: valor={}", expense.getValue());
                    return ResponseEntity.ok(expenseDTO);
                })
                .orElseGet(() -> {
                    logger.warn("Despesa com ID {} não encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByUser(@PathVariable Long userId) {
        logger.info("GET /api/expenses/user/{} - Buscando despesas do usuário", userId);
        
        return userService.findById(userId)
                .map(user -> {
                    List<Expense> expenses = expenseService.findByUser(user);
                    List<ExpenseResponseDTO> expenseDTOs = expenses.stream()
                            .map(ExpenseMapper::toDTO)
                            .collect(Collectors.toList());
                    logger.info("Encontradas {} despesas para o usuário {}", expenseDTOs.size(), userId);
                    return ResponseEntity.ok(expenseDTOs);
                })
                .orElseGet(() -> {
                    logger.warn("Usuário com ID {} não encontrado", userId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByCategory(@PathVariable Long categoryId) {
        logger.info("GET /api/expenses/category/{} - Buscando despesas da categoria", categoryId);
        
        return categoryService.findById(categoryId)
                .map(category -> {
                    List<Expense> expenses = expenseService.findByCategory(category);
                    List<ExpenseResponseDTO> expenseDTOs = expenses.stream()
                            .map(ExpenseMapper::toDTO)
                            .collect(Collectors.toList());
                    logger.info("Encontradas {} despesas para a categoria {}", expenseDTOs.size(), categoryId);
                    return ResponseEntity.ok(expenseDTOs);
                })
                .orElseGet(() -> {
                    logger.warn("Categoria com ID {} não encontrada", categoryId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<?> createExpense(@Valid @RequestBody ExpenseRequestDTO expenseRequestDTO) {
        logger.info("POST /api/expenses - Criando nova despesa");
        
        try {
            Category category = categoryService.findById(expenseRequestDTO.categoryId())
                .orElseThrow(() -> {
                    logger.error("Categoria com ID {} não encontrada", expenseRequestDTO.categoryId());
                    return new RuntimeException("Categoria não encontrada");
                });
                
            User user = userService.findById(expenseRequestDTO.userId())
                .orElseThrow(() -> {
                    logger.error("Usuário com ID {} não encontrado", expenseRequestDTO.userId());
                    return new RuntimeException("Usuário não encontrado");
                });

            Expense expense = ExpenseMapper.toEntity(expenseRequestDTO, category, user);
            Expense savedExpense = expenseService.save(expense);
            ExpenseResponseDTO responseDTO = ExpenseMapper.toDTO(savedExpense);
            
            logger.info("Despesa criada com ID: {} - valor: {}", savedExpense.getId(), expenseRequestDTO.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            
        } catch (RuntimeException e) {
            logger.error("Erro ao criar despesa: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO expenseRequestDTO) {
        logger.info("PUT /api/expenses/{} - Atualizando despesa", id);
        
        try {
            return expenseService.findById(id)
                .map(existingExpense -> {
                    Category category = categoryService.findById(expenseRequestDTO.categoryId())
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
                    
                    User user = userService.findById(expenseRequestDTO.userId())
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                    
                    ExpenseMapper.updateEntityWithRelations(existingExpense, expenseRequestDTO, category, user);
                    Expense updated = expenseService.save(existingExpense);
                    ExpenseResponseDTO responseDTO = ExpenseMapper.toDTO(updated);
                    
                    logger.info("Despesa {} atualizada com sucesso", id);
                    return ResponseEntity.ok((Object) responseDTO);
                })
                .orElseGet(() -> {
                    logger.warn("Despesa com ID {} não encontrada para atualização", id);
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            logger.error("Erro ao atualizar despesa: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        logger.info("DELETE /api/expenses/{} - Deletando despesa", id);
        
        return expenseService.findById(id)
                .map(expense -> {
                    expenseService.deleteExpense(id);
                    logger.info("Despesa {} deletada com sucesso", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Despesa com ID {} não encontrada para deleção", id);
                    return ResponseEntity.notFound().build();
                });
    }
}