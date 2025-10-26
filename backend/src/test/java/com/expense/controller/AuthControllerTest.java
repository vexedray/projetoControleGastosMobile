package com.expense.controller;

import com.expense.dto.request.LoginRequestDTO;
import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.LoginResponseDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.UserMapper;
import com.expense.model.User;
import com.expense.service.AuthService;
import com.expense.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private UserRequestDTO registerRequestDTO;
    private LoginRequestDTO loginRequestDTO;
    private UserResponseDTO userResponseDTO;
    private LoginResponseDTO loginResponseDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encoded_password");

        registerRequestDTO = new UserRequestDTO();
        registerRequestDTO.setName("John Doe");
        registerRequestDTO.setEmail("john@example.com");
        registerRequestDTO.setPassword("password123");

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("john@example.com");
        loginRequestDTO.setPassword("password123");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("John Doe");
        userResponseDTO.setEmail("john@example.com");

        loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken("jwt_token_here");
        loginResponseDTO.setUser(userResponseDTO);
    }

    @Test
    void register_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        when(authService.register(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(authService, times(1)).register(any(UserRequestDTO.class));
    }

    @Test
    void register_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO invalidDTO = new UserRequestDTO();
        invalidDTO.setName("");
        invalidDTO.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(UserRequestDTO.class));
    }

    @Test
    void register_WithDuplicateEmail_ShouldReturnConflict() throws Exception {
        // Arrange
        when(authService.register(any(UserRequestDTO.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDTO)))
                .andExpect(status().isConflict());

        verify(authService, times(1)).register(any(UserRequestDTO.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokenAndUser() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt_token_here"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));

        verify(authService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    void login_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        LoginRequestDTO invalidDTO = new LoginRequestDTO();
        invalidDTO.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequestDTO.class));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    void checkEmailAvailability_WhenEmailAvailable_ShouldReturnTrue() throws Exception {
        // Arrange
        when(userService.existsByEmail("newemail@example.com")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", "newemail@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.email").value("newemail@example.com"));

        verify(userService, times(1)).existsByEmail("newemail@example.com");
    }

    @Test
    void checkEmailAvailability_WhenEmailTaken_ShouldReturnFalse() throws Exception {
        // Arrange
        when(userService.existsByEmail("john@example.com")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", "john@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).existsByEmail("john@example.com");
    }

    @Test
    void checkEmailAvailability_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", "invalid-email")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).existsByEmail(anyString());
    }
}
