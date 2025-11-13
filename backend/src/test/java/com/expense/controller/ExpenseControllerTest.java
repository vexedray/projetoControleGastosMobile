package com.expense.controller;

import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.User;
import com.expense.service.CategoryService;
import com.expense.service.ExpenseService;
import com.expense.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
@DisplayName("Testes do ExpenseController")
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserService userService;

    private Expense expense1;
    private Expense expense2;
    private Category category;
    private User user;
    private ExpenseRequestDTO expenseRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setPassword("senha123");
        user.setCreatedAt(LocalDateTime.now());

        category = new Category();
        category.setId(1L);
        category.setName("Alimentação");
        category.setDescription("Despesas com alimentação");
        category.setCreatedAt(LocalDateTime.now());

        expense1 = new Expense();
        expense1.setId(1L);
        expense1.setValue(new BigDecimal("50.00"));
        expense1.setDate(LocalDateTime.now());
        expense1.setCategory(category);
        expense1.setUser(user);

        expense2 = new Expense();
        expense2.setId(2L);
        expense2.setValue(new BigDecimal("80.00"));
        expense2.setDate(LocalDateTime.now());
        expense2.setCategory(category);
        expense2.setUser(user);

        expenseRequestDTO = new ExpenseRequestDTO(
            new BigDecimal("50.00"),
            LocalDateTime.now(),
            1L,
            1L
        );
    }

    @Test
    @DisplayName("Deve retornar todas as despesas")
    void getAllExpenses_ShouldReturnAllExpenses() throws Exception {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense1, expense2);
        when(expenseService.findAll()).thenReturn(expenses);

        // Act & Assert
        mockMvc.perform(get("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].value", is(50.00)))
                .andExpect(jsonPath("$[1].value", is(80.00)));

        verify(expenseService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar despesa por ID quando existir")
    void getExpenseById_WhenExpenseExists_ShouldReturnExpense() throws Exception {
        // Arrange
        when(expenseService.findById(1L)).thenReturn(Optional.of(expense1));

        // Act & Assert
        mockMvc.perform(get("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.value", is(50.00)));

        verify(expenseService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando despesa não existir por ID")
    void getExpenseById_WhenExpenseNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(expenseService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve retornar despesas por usuário")
    void getExpensesByUser_WhenUserExists_ShouldReturnExpenses() throws Exception {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense1, expense2);
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(expenseService.findByUser(user)).thenReturn(expenses);

        // Act & Assert
        mockMvc.perform(get("/api/expenses/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].value", is(50.00)))
                .andExpect(jsonPath("$[1].value", is(80.00)));

        verify(userService, times(1)).findById(1L);
        verify(expenseService, times(1)).findByUser(user);
    }

    @Test
    @DisplayName("Deve retornar 404 quando usuário não existir ao buscar despesas")
    void getExpensesByUser_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(userService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/expenses/user/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(999L);
        verify(expenseService, never()).findByUser(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar despesas por categoria")
    void getExpensesByCategory_WhenCategoryExists_ShouldReturnExpenses() throws Exception {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense1, expense2);
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(expenseService.findByCategory(category)).thenReturn(expenses);

        // Act & Assert
        mockMvc.perform(get("/api/expenses/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].value", is(50.00)))
                .andExpect(jsonPath("$[1].value", is(80.00)));

        verify(categoryService, times(1)).findById(1L);
        verify(expenseService, times(1)).findByCategory(category);
    }

    @Test
    @DisplayName("Deve retornar 404 quando categoria não existir ao buscar despesas")
    void getExpensesByCategory_WhenCategoryNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/expenses/category/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(999L);
        verify(expenseService, never()).findByCategory(any(Category.class));
    }

    @Test
    @DisplayName("Deve criar nova despesa com sucesso")
    void createExpense_WithValidData_ShouldCreateExpense() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(expenseService.save(any(Expense.class))).thenReturn(expense1);

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value", is(50.00)));

        verify(categoryService, times(1)).findById(1L);
        verify(userService, times(1)).findById(1L);
        verify(expenseService, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar despesa com categoria inexistente")
    void createExpense_WithInvalidCategory_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        ExpenseRequestDTO invalidDTO = new ExpenseRequestDTO(
            new BigDecimal("50.00"),
            LocalDateTime.now(),
            999L,
            1L
        );

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Categoria não encontrada"));

        verify(categoryService, times(1)).findById(999L);
        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar despesa com usuário inexistente")
    void createExpense_WithInvalidUser_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(userService.findById(999L)).thenReturn(Optional.empty());

        ExpenseRequestDTO invalidDTO = new ExpenseRequestDTO(
            new BigDecimal("50.00"),
            LocalDateTime.now(),
            1L,
            999L
        );

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuário não encontrado"));

        verify(categoryService, times(1)).findById(1L);
        verify(userService, times(1)).findById(999L);
        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar despesa com dados inválidos")
    void createExpense_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ExpenseRequestDTO invalidDTO = new ExpenseRequestDTO(
            new BigDecimal("-10.00"),
            null,
            null,
            null
        );

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve atualizar despesa existente")
    void updateExpense_WhenExpenseExists_ShouldUpdateExpense() throws Exception {
        // Arrange
        ExpenseRequestDTO updateDTO = new ExpenseRequestDTO(
            new BigDecimal("60.00"),
            LocalDateTime.now(),
            1L,
            1L
        );

        Expense updatedExpense = new Expense();
        updatedExpense.setId(1L);
        updatedExpense.setValue(new BigDecimal("60.00"));
        updatedExpense.setDate(LocalDateTime.now());
        updatedExpense.setCategory(category);
        updatedExpense.setUser(user);

        when(expenseService.findById(1L)).thenReturn(Optional.of(expense1));
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(expenseService.save(any(Expense.class))).thenReturn(updatedExpense);

        // Act & Assert
        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(60.00)));

        verify(expenseService, times(1)).findById(1L);
        verify(categoryService, times(1)).findById(1L);
        verify(userService, times(1)).findById(1L);
        verify(expenseService, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar despesa inexistente")
    void updateExpense_WhenExpenseNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(expenseService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequestDTO)))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).findById(999L);
        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar despesa com categoria inexistente")
    void updateExpense_WithInvalidCategory_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ExpenseRequestDTO updateDTO = new ExpenseRequestDTO(
            new BigDecimal("50.00"),
            LocalDateTime.now(),
            999L,
            1L
        );

        when(expenseService.findById(1L)).thenReturn(Optional.of(expense1));
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Categoria não encontrada"));

        verify(expenseService, times(1)).findById(1L);
        verify(categoryService, times(1)).findById(999L);
        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve deletar despesa existente")
    void deleteExpense_WhenExpenseExists_ShouldDeleteExpense() throws Exception {
        // Arrange
        when(expenseService.findById(1L)).thenReturn(Optional.of(expense1));
        doNothing().when(expenseService).deleteExpense(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(expenseService, times(1)).findById(1L);
        verify(expenseService, times(1)).deleteExpense(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar despesa inexistente")
    void deleteExpense_WhenExpenseNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(expenseService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).findById(999L);
        verify(expenseService, never()).deleteExpense(anyLong());
    }
}
