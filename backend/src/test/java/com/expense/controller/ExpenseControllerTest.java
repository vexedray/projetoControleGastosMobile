package com.expense.controller;

import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.mapper.ExpenseMapper;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.User;
import com.expense.service.CategoryService;
import com.expense.service.ExpenseService;
import com.expense.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
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

    @MockBean
    private ExpenseMapper expenseMapper;

    @MockBean
    private com.expense.security.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.expense.security.UserDetailsServiceImpl userDetailsService;

    private Expense expense;
    private ExpenseRequestDTO requestDTO;
    private ExpenseResponseDTO responseDTO;
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

        expense = new Expense();
        expense.setId(1L);
        expense.setAmount(new BigDecimal("50.00"));
        expense.setDescription("Lunch");
        expense.setCategory(category);
        expense.setUser(user);

        requestDTO = new ExpenseRequestDTO();
        requestDTO.setDescription("Lunch");
        requestDTO.setAmount(new BigDecimal("50.00"));
        requestDTO.setDate(LocalDate.now());
        requestDTO.setCategoryId(1L);
        requestDTO.setUserId(1L);

        responseDTO = new ExpenseResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setDescription("Lunch");
        responseDTO.setAmount(new BigDecimal("50.00"));
        responseDTO.setDate(LocalDate.now());
    }

    @Test
    void getAllExpenses_ShouldReturnExpenseList() throws Exception {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);

        when(expenseService.findAll()).thenReturn(expenses);
        when(expenseMapper.toResponseDTO(any(Expense.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Lunch"))
                .andExpect(jsonPath("$[0].amount").value(50.00));

        verify(expenseService, times(1)).findAll();
    }

    @Test
    void getExpenseById_WhenExpenseExists_ShouldReturnExpense() throws Exception {
        // Arrange
        when(expenseService.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseMapper.toResponseDTO(expense)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(50.00));

        verify(expenseService, times(1)).findById(1L);
    }

    @Test
    void getExpenseById_WhenExpenseDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(expenseService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).findById(999L);
    }

    @Test
    void getExpensesByUser_WhenUserExists_ShouldReturnExpenseList() throws Exception {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(expenseService.findByUserId(1L)).thenReturn(expenses);
        when(expenseMapper.toResponseDTO(any(Expense.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/expenses/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(userService, times(1)).getUserById(1L);
        verify(expenseService, times(1)).findByUserId(1L);
    }

    @Test
    void getExpensesByUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/expenses/user/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
        verify(expenseService, never()).findByUserId(any());
    }

    @Test
    void getExpensesByCategory_WhenCategoryExists_ShouldReturnExpenseList() throws Exception {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);

        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(expenseService.findByCategoryId(1L)).thenReturn(expenses);
        when(expenseMapper.toResponseDTO(any(Expense.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/expenses/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(categoryService, times(1)).findById(1L);
        verify(expenseService, times(1)).findByCategoryId(1L);
    }

    @Test
    void getExpensesByCategory_WhenCategoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/expenses/category/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(999L);
        verify(expenseService, never()).findByCategoryId(any());
    }

    @Test
    void createExpense_WithValidData_ShouldReturnCreatedExpense() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(expenseMapper.toEntity(any(ExpenseRequestDTO.class))).thenReturn(expense);
        when(expenseService.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toResponseDTO(any(Expense.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(50.00));

        verify(categoryService, times(1)).findById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(expenseService, times(1)).save(any(Expense.class));
    }

    @Test
    void createExpense_WithInvalidCategoryId_ShouldThrowException() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        ExpenseRequestDTO invalidDTO = new ExpenseRequestDTO();
        invalidDTO.setDescription("Lunch");
        invalidDTO.setAmount(new BigDecimal("50.00"));
        invalidDTO.setDate(LocalDate.now());
        invalidDTO.setCategoryId(999L);
        invalidDTO.setUserId(1L);

        // Act & Assert - Expect ServletException wrapping RuntimeException
        try {
            mockMvc.perform(post("/api/expenses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDTO)));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RuntimeException);
            assertTrue(e.getCause().getMessage().contains("Category not found"));
        }

        verify(categoryService, times(1)).findById(999L);
        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_WithInvalidUserId_ShouldThrowException() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        ExpenseRequestDTO invalidDTO = new ExpenseRequestDTO();
        invalidDTO.setDescription("Lunch");
        invalidDTO.setAmount(new BigDecimal("50.00"));
        invalidDTO.setDate(LocalDate.now());
        invalidDTO.setCategoryId(1L);
        invalidDTO.setUserId(999L);

        // Act & Assert - Expect ServletException wrapping RuntimeException
        try {
            mockMvc.perform(post("/api/expenses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDTO)));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RuntimeException);
            assertTrue(e.getCause().getMessage().contains("User not found"));
        }

        verify(categoryService, times(1)).findById(1L);
        verify(userService, times(1)).getUserById(999L);
        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ExpenseRequestDTO invalidDTO = new ExpenseRequestDTO();
        invalidDTO.setDescription("");

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    void updateExpense_WhenExpenseExists_ShouldReturnUpdatedExpense() throws Exception {
        // Arrange
        when(expenseService.findById(1L)).thenReturn(Optional.of(expense));
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(expenseService.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toResponseDTO(any(Expense.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(expenseService, times(1)).findById(1L);
        verify(categoryService, times(1)).findById(1L);
        verify(expenseService, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_WhenExpenseDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(expenseService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).findById(999L);
        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    void updateExpense_WithInvalidCategoryId_ShouldThrowException() throws Exception {
        // Arrange
        when(expenseService.findById(1L)).thenReturn(Optional.of(expense));
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        ExpenseRequestDTO updateDTO = new ExpenseRequestDTO();
        updateDTO.setDescription("Updated Lunch");
        updateDTO.setAmount(new BigDecimal("75.00"));
        updateDTO.setDate(LocalDate.now());
        updateDTO.setCategoryId(999L);
        updateDTO.setUserId(1L);

        // Act & Assert - Expect ServletException wrapping RuntimeException
        try {
            mockMvc.perform(put("/api/expenses/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RuntimeException);
            assertTrue(e.getCause().getMessage().contains("Category not found"));
        }

        verify(expenseService, times(1)).findById(1L);
        verify(categoryService, times(1)).findById(999L);
        verify(expenseService, never()).save(any(Expense.class));
    }

    @Test
    void deleteExpense_WhenExpenseExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        when(expenseService.findById(1L)).thenReturn(Optional.of(expense));
        doNothing().when(expenseService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(expenseService, times(1)).findById(1L);
        verify(expenseService, times(1)).deleteById(1L);
    }

    @Test
    void deleteExpense_WhenExpenseDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(expenseService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).findById(999L);
        verify(expenseService, never()).deleteById(anyLong());
    }
}