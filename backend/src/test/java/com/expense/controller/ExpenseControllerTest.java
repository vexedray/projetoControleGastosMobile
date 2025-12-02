package com.expense.controller;

import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.ExpenseMapper;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.User;
import com.expense.model.hateoas.ExpenseModel;
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
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "test@example.com")
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

    @MockBean
    private com.expense.repository.UserRepository userRepository;

    @MockBean
    private com.expense.assembler.ExpenseModelAssembler expenseModelAssembler;

    private Expense expense;
    private ExpenseRequestDTO requestDTO;
    private ExpenseResponseDTO responseDTO;
    private ExpenseModel expenseModel;
    private User mockUser;
    private User user;
    private Category category;
    private UserResponseDTO userResponseDTO;
    private CategoryResponseDTO categoryResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup mock authenticated user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        
        // Mock userRepository
        when(userRepository.findByEmail(anyString())).thenReturn(mockUser);

        // Setup user
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // Setup category
        category = new Category();
        category.setId(1L);
        category.setName("Food");

        // Setup expense
        expense = new Expense();
        expense.setId(1L);
        expense.setAmount(new BigDecimal("50.00"));
        expense.setDescription("Lunch");
        expense.setDate(LocalDate.now().atStartOfDay()); // LocalDateTime
        expense.setCategory(category);
        expense.setUser(user);

        // Setup request DTO - IMPORTANTE: incluir todos os campos obrigatórios
        // Mesmo que o userId não seja usado (vem da auth), precisa estar no DTO por causa do @NotNull
        requestDTO = new ExpenseRequestDTO();
        requestDTO.setDescription("Lunch");
        requestDTO.setAmount(new BigDecimal("50.00"));
        requestDTO.setDate(LocalDate.now());
        requestDTO.setCategoryId(1L);
        requestDTO.setUserId(1L); // Obrigatório por causa da validação @NotNull

        // Setup response DTOs
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("John Doe");

        categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(1L);
        categoryResponseDTO.setName("Food");

        responseDTO = new ExpenseResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setDescription("Lunch");
        responseDTO.setAmount(new BigDecimal("50.00"));
        responseDTO.setDate(LocalDate.now());
        responseDTO.setUser(userResponseDTO);
        responseDTO.setCategory(categoryResponseDTO);

        // Setup ExpenseModel (HATEOAS)
        expenseModel = new ExpenseModel(
            1L,
            new BigDecimal("50.00"),
            "Lunch",
            LocalDate.now(),
            1L,
            "John Doe",
            1L,
            "Food"
        );
        expenseModel.add(Link.of("/api/expenses/1", "self"));
        expenseModel.add(Link.of("/api/expenses", "expenses"));

        // Mock ExpenseModelAssembler
        when(expenseModelAssembler.toModel(any(ExpenseResponseDTO.class)))
            .thenReturn(expenseModel);
    }

    @Test
    void getAllExpenses_ShouldReturnExpenseList() throws Exception {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);
        when(expenseService.getAllExpenses(1L)).thenReturn(expenses);
        when(expenseMapper.toResponseDTO(any(Expense.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.expenseModelList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.expenseModelList[0].description").value("Lunch"));

        verify(expenseService, times(1)).getAllExpenses(1L);
    }

    @Test
    void getExpenseById_WhenExpenseExists_ShouldReturnExpense() throws Exception {
        // Arrange
        when(expenseService.getExpenseById(1L, 1L)).thenReturn(Optional.of(expense));
        when(expenseMapper.toResponseDTO(expense)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Lunch"));

        verify(expenseService, times(1)).getExpenseById(1L, 1L);
    }

    @Test
    void getExpenseById_WhenExpenseDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(expenseService.getExpenseById(999L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).getExpenseById(999L, 1L);
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
                .andExpect(jsonPath("$._embedded.expenseModelList[0].id").value(1));

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
                .andExpect(jsonPath("$._embedded.expenseModelList[0].id").value(1));

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
        when(expenseService.createExpense(any(ExpenseRequestDTO.class), eq(1L))).thenReturn(expense);
        when(expenseMapper.toResponseDTO(any(Expense.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Lunch"));

        verify(expenseService, times(1)).createExpense(any(ExpenseRequestDTO.class), eq(1L));
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

        verify(expenseService, never()).createExpense(any(ExpenseRequestDTO.class), anyLong());
    }

    @Test
    void updateExpense_WhenExpenseExists_ShouldReturnUpdatedExpense() throws Exception {
        // Arrange
        when(expenseService.updateExpense(eq(1L), any(ExpenseRequestDTO.class), eq(1L))).thenReturn(expense);
        when(expenseMapper.toResponseDTO(any(Expense.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(expenseService, times(1)).updateExpense(eq(1L), any(ExpenseRequestDTO.class), eq(1L));
    }

    @Test
    void updateExpense_WhenExpenseDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(expenseService.updateExpense(eq(999L), any(ExpenseRequestDTO.class), eq(1L)))
            .thenThrow(new RuntimeException("Expense not found"));

        // Act & Assert
        mockMvc.perform(put("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).updateExpense(eq(999L), any(ExpenseRequestDTO.class), eq(1L));
    }

    @Test
    void deleteExpense_WhenExpenseExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(expenseService).deleteExpense(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(expenseService, times(1)).deleteExpense(1L, 1L);
    }

    @Test
    void deleteExpense_WhenExpenseDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Expense not found"))
            .when(expenseService).deleteExpense(999L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/expenses/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).deleteExpense(999L, 1L);
    }
}