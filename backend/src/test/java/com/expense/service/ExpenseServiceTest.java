package com.expense.service;

import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.User;
import com.expense.repository.CategoryRepository;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense expense;
    private User user;
    private Category category;
    private ExpenseRequestDTO expenseRequestDTO;

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
        category.setUser(user);

        expense = new Expense();
        expense.setId(1L);
        expense.setDescription("Lunch");
        expense.setAmount(new BigDecimal("50.00"));
        expense.setDate(LocalDate.now().atStartOfDay());
        expense.setCategory(category);
        expense.setUser(user);

        expenseRequestDTO = new ExpenseRequestDTO();
        expenseRequestDTO.setDescription("Lunch");
        expenseRequestDTO.setAmount(new BigDecimal("50.00"));
        expenseRequestDTO.setDate(LocalDate.now());
        expenseRequestDTO.setCategoryId(1L);
        expenseRequestDTO.setUserId(1L);
    }

    @Test
    void getAllExpenses_ShouldReturnUserExpenses() {
        // Arrange
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setAmount(new BigDecimal("100.00"));
        expense2.setUser(user);

        List<Expense> expenses = Arrays.asList(expense, expense2);
        when(expenseRepository.findByUserId(1L)).thenReturn(expenses);

        // Act
        List<Expense> result = expenseService.getAllExpenses(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("50.00"), result.get(0).getAmount());
        assertEquals(new BigDecimal("100.00"), result.get(1).getAmount());
        verify(expenseRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getExpenseById_WhenExpenseExistsAndBelongsToUser_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(expense));

        // Act
        Optional<Expense> result = expenseService.getExpenseById(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("50.00"), result.get().getAmount());
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    void getExpenseById_WhenExpenseDoesNotBelongToUser_ShouldReturnEmpty() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // Act
        Optional<Expense> result = expenseService.getExpenseById(1L, 999L);

        // Assert
        assertFalse(result.isPresent());
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, 999L);
    }

    @Test
    void createExpense_WithValidData_ShouldCreateAndReturnExpense() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        // Act
        Expense result = expenseService.createExpense(expenseRequestDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        assertEquals("Lunch", result.getDescription());
        verify(userRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void createExpense_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            expenseService.createExpense(expenseRequestDTO, 999L);
        });

        verify(userRepository, times(1)).findById(999L);
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_WhenCategoryNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            expenseService.createExpense(expenseRequestDTO, 1L);
        });

        verify(userRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void updateExpense_WhenExpenseExists_ShouldUpdateAndReturn() {
        // Arrange
        ExpenseRequestDTO updateDTO = new ExpenseRequestDTO();
        updateDTO.setDescription("Updated Lunch");
        updateDTO.setAmount(new BigDecimal("75.00"));
        updateDTO.setDate(LocalDate.now());
        updateDTO.setCategoryId(1L);

        Expense updatedExpense = new Expense();
        updatedExpense.setId(1L);
        updatedExpense.setDescription("Updated Lunch");
        updatedExpense.setAmount(new BigDecimal("75.00"));
        updatedExpense.setDate(LocalDate.now().atStartOfDay());
        updatedExpense.setCategory(category);
        updatedExpense.setUser(user);

        when(expenseRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(expense));
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);

        // Act
        Expense result = expenseService.updateExpense(1L, updateDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Lunch", result.getDescription());
        assertEquals(new BigDecimal("75.00"), result.getAmount());
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_WhenExpenseDoesNotBelongToUser_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            expenseService.updateExpense(1L, expenseRequestDTO, 999L);
        });

        verify(expenseRepository, times(1)).findByIdAndUserId(1L, 999L);
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void deleteExpense_WhenExpenseExists_ShouldDelete() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(expense));
        doNothing().when(expenseRepository).delete(expense);

        // Act
        expenseService.deleteExpense(1L, 1L);

        // Assert
        verify(expenseRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(expenseRepository, times(1)).delete(expense);
    }

    @Test
    void deleteExpense_WhenExpenseDoesNotBelongToUser_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            expenseService.deleteExpense(1L, 999L);
        });

        verify(expenseRepository, times(1)).findByIdAndUserId(1L, 999L);
        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    void findAll_ShouldReturnAllExpenses() {
        // Arrange
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setAmount(new BigDecimal("100.00"));

        List<Expense> expenses = Arrays.asList(expense, expense2);
        when(expenseRepository.findAll()).thenReturn(expenses);

        // Act
        List<Expense> result = expenseService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenExpenseExists_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // Act
        Optional<Expense> result = expenseService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("50.00"), result.get().getAmount());
        verify(expenseRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenExpenseDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Expense> result = expenseService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(expenseRepository, times(1)).findById(999L);
    }

    @Test
    void findByUserId_ShouldReturnUserExpenses() {
        // Arrange
        List<Expense> userExpenses = Arrays.asList(expense);
        when(expenseRepository.findByUserId(1L)).thenReturn(userExpenses);

        // Act
        List<Expense> result = expenseService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expense.getAmount(), result.get(0).getAmount());
        verify(expenseRepository, times(1)).findByUserId(1L);
    }

    @Test
    void findByUserId_WhenUserHasNoExpenses_ShouldReturnEmptyList() {
        // Arrange
        when(expenseRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        // Act
        List<Expense> result = expenseService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(expenseRepository, times(1)).findByUserId(1L);
    }

    @Test
    void findByCategoryId_ShouldReturnCategoryExpenses() {
        // Arrange
        List<Expense> categoryExpenses = Arrays.asList(expense);
        when(expenseRepository.findByCategoryId(1L)).thenReturn(categoryExpenses);

        // Act
        List<Expense> result = expenseService.findByCategoryId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expense.getAmount(), result.get(0).getAmount());
        verify(expenseRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    void findByCategoryId_WhenCategoryHasNoExpenses_ShouldReturnEmptyList() {
        // Arrange
        when(expenseRepository.findByCategoryId(1L)).thenReturn(Arrays.asList());

        // Act
        List<Expense> result = expenseService.findByCategoryId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(expenseRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    void save_ShouldSaveAndReturnExpense() {
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
    void deleteById_ShouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(expenseRepository).deleteById(1L);

        // Act
        expenseService.deleteById(1L);

        // Assert
        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    void existsById_WhenExpenseExists_ShouldReturnTrue() {
        // Arrange
        when(expenseRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = expenseService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(expenseRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_WhenExpenseDoesNotExist_ShouldReturnFalse() {
        // Arrange
        when(expenseRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = expenseService.existsById(999L);

        // Assert
        assertFalse(result);
        verify(expenseRepository, times(1)).existsById(999L);
    }
}