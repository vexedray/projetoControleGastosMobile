package com.expense.controller;

import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.service.ExpenseService;
import com.expense.service.CategoryService;
import com.expense.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<Expense>> getAllExpenses() {
        logger.info("GET /api/expenses - Buscando todas as despesas");
        List<Expense> expenses = expenseService.findAll();
        logger.info("Encontradas {} despesas", expenses.size());
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        logger.info("GET /api/expenses/{} - Buscando despesa por ID", id);
        return expenseService.findById(id)
                .map(expense -> {
                    logger.info("Despesa encontrada: valor={}", expense.getValue());
                    return ResponseEntity.ok(expense);
                })
                .orElseGet(() -> {
                    logger.warn("Despesa com ID {} não encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getExpensesByUser(@PathVariable Long userId) {
        logger.info("GET /api/expenses/user/{} - Buscando despesas do usuário", userId);
        
        return userService.findById(userId)
                .map(user -> {
                    List<Expense> expenses = expenseService.findByUser(user);
                    logger.info("Encontradas {} despesas para o usuário {}", expenses.size(), userId);
                    return ResponseEntity.ok(expenses);
                })
                .orElseGet(() -> {
                    logger.warn("Usuário com ID {} não encontrado", userId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable Long categoryId) {
        logger.info("GET /api/expenses/category/{} - Buscando despesas da categoria", categoryId);
        
        return categoryService.findById(categoryId)
                .map(category -> {
                    List<Expense> expenses = expenseService.findByCategory(category);
                    logger.info("Encontradas {} despesas para a categoria {}", expenses.size(), categoryId);
                    return ResponseEntity.ok(expenses);
                })
                .orElseGet(() -> {
                    logger.warn("Categoria com ID {} não encontrada", categoryId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody Map<String, Object> expenseData) {
        logger.info("POST /api/expenses - Criando nova despesa");
        
        try {
            // Validação dos campos obrigatórios
            if (!expenseData.containsKey("value") || 
                !expenseData.containsKey("categoryId") || 
                !expenseData.containsKey("userId")) {
                logger.error("Campos obrigatórios ausentes");
                return ResponseEntity.badRequest()
                    .body("Campos obrigatórios: value, categoryId, userId");
            }
            
            BigDecimal value = new BigDecimal(expenseData.get("value").toString());
            Long categoryId = Long.valueOf(expenseData.get("categoryId").toString());
            Long userId = Long.valueOf(expenseData.get("userId").toString());

            // Validação de valor positivo
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("Valor da despesa deve ser positivo");
                return ResponseEntity.badRequest()
                    .body("O valor da despesa deve ser maior que zero");
            }

            Category category = categoryService.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Categoria com ID {} não encontrada", categoryId);
                    return new RuntimeException("Categoria não encontrada");
                });
                
            User user = userService.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Usuário com ID {} não encontrado", userId);
                    return new RuntimeException("Usuário não encontrado");
                });

            Expense expense = new Expense(value, category, user);
            Expense savedExpense = expenseService.save(expense);
            
            logger.info("Despesa criada com ID: {} - valor: {}", savedExpense.getId(), value);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
            
        } catch (NumberFormatException e) {
            logger.error("Formato de número inválido", e);
            return ResponseEntity.badRequest()
                .body("Formato de número inválido para value, categoryId ou userId");
        } catch (RuntimeException e) {
            logger.error("Erro ao criar despesa: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @PathVariable Long id,
            @RequestBody Map<String, Object> expenseData) {
        logger.info("PUT /api/expenses/{} - Atualizando despesa", id);
        
        try {
            return expenseService.findById(id)
                .map(existingExpense -> {
                    if (expenseData.containsKey("value")) {
                        BigDecimal value = new BigDecimal(expenseData.get("value").toString());
                        if (value.compareTo(BigDecimal.ZERO) <= 0) {
                            return ResponseEntity.badRequest()
                                .body("O valor da despesa deve ser maior que zero");
                        }
                        existingExpense.setValue(value);
                    }
                    
                    if (expenseData.containsKey("categoryId")) {
                        Long categoryId = Long.valueOf(expenseData.get("categoryId").toString());
                        Category category = categoryService.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
                        existingExpense.setCategory(category);
                    }
                    
                    Expense updated = expenseService.save(existingExpense);
                    logger.info("Despesa {} atualizada com sucesso", id);
                    return ResponseEntity.ok((Object) updated);
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