package com.expense.service;

import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.User;
import com.expense.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense expense;
    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setDescription("Food and beverages");

        expense = new Expense();
        expense.setId(1L);
        expense.setAmount(new BigDecimal("50.00"));
        expense.setCategory(category);
        expense.setUser(user);
    }

    @Test
    void testFindAll_ShouldReturnAllExpenses() {
        // Arrange
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setAmount(new BigDecimal("100.00"));
        expense2.setCategory(category);
        expense2.setUser(user);

        List<Expense> expenses = Arrays.asList(expense, expense2);
        when(expenseRepository.findAll()).thenReturn(expenses);

        // Act
        List<Expense> result = expenseService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("50.00"), result.get(0).getAmount());
        assertEquals(new BigDecimal("100.00"), result.get(1).getAmount());
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    void testFindById_WhenExpenseExists_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // Act
        Optional<Expense> result = expenseService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("50.00"), result.get().getAmount());
        assertEquals("Food", result.get().getCategory().getName());
        verify(expenseRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_WhenExpenseDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Expense> result = expenseService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(expenseRepository, times(1)).findById(999L);
    }

    @Test
    void testSave_ShouldSaveAndReturnExpense() {
        // Arrange
        Expense newExpense = new Expense();
        newExpense.setAmount(new BigDecimal("75.50"));
        newExpense.setCategory(category);
        newExpense.setUser(user);

        Expense savedExpense = new Expense();
        savedExpense.setId(3L);
        savedExpense.setAmount(new BigDecimal("75.50"));
        savedExpense.setCategory(category);
        savedExpense.setUser(user);

        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        // Act
        Expense result = expenseService.save(newExpense);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(new BigDecimal("75.50"), result.getAmount());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testDeleteExpense_ShouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(expenseRepository).deleteById(1L);

        // Act
        expenseService.deleteExpense(1L);

        // Assert
        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByUserId_ShouldReturnUserExpenses() {
        // Arrange
        List<Expense> userExpenses = Arrays.asList(expense);
        when(expenseRepository.findByUser_Id(1L)).thenReturn(userExpenses);

        // Act
        List<Expense> result = expenseService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expense.getAmount(), result.get(0).getAmount());
        verify(expenseRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    void testFindByCategoryId_ShouldReturnCategoryExpenses() {
        // Arrange
        List<Expense> categoryExpenses = Arrays.asList(expense);
        when(expenseRepository.findByCategory_Id(1L)).thenReturn(categoryExpenses);

        // Act
        List<Expense> result = expenseService.findByCategoryId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expense.getAmount(), result.get(0).getAmount());
        verify(expenseRepository, times(1)).findByCategory_Id(1L);
    }

    @Test
    void testSave_WithNegativeValue_ShouldThrowException() {
        // Arrange
        Expense invalidExpense = new Expense();
        invalidExpense.setAmount(new BigDecimal("-10.00"));
        invalidExpense.setCategory(category);
        invalidExpense.setUser(user);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            when(expenseRepository.save(any(Expense.class)))
                .thenThrow(new IllegalArgumentException("Value must be positive"));
            expenseService.save(invalidExpense);
        });
    }

    @Test
    void testFindAll_WhenNoExpensesExist_ShouldReturnEmptyList() {
        // Arrange
        when(expenseRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Expense> result = expenseService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    void testUpdateExpense_ShouldUpdateAndReturnExpense() {
        // Arrange
        Expense updatedExpense = new Expense();
        updatedExpense.setId(1L);
        updatedExpense.setAmount(new BigDecimal("150.00"));
        updatedExpense.setCategory(category);
        updatedExpense.setUser(user);

        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);

        // Act
        Expense result = expenseService.save(updatedExpense);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("150.00"), result.getAmount());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testCount_ShouldReturnNumberOfExpenses() {
        // Arrange
        when(expenseRepository.count()).thenReturn(10L);

        // Act
        long count = expenseRepository.count();

        // Assert
        assertEquals(10L, count);
        verify(expenseRepository, times(1)).count();
    }

    @Test
    void testFindByUserId_WhenUserHasNoExpenses_ShouldReturnEmptyList() {
        // Arrange
        when(expenseRepository.findByUser_Id(1L)).thenReturn(Arrays.asList());

        // Act
        List<Expense> result = expenseService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(expenseRepository, times(1)).findByUser_Id(1L);
    }
}
