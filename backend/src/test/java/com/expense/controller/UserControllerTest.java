package com.expense.controller;

import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.UserMapper;
import com.expense.model.User;
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

@WebMvcTest(UserController.class)
@DisplayName("Testes do UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User user1;
    private User user2;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("João Silva");
        user1.setEmail("joao@email.com");
        user1.setPassword("senha123");
        user1.setCreatedAt(LocalDateTime.now());

        user2 = new User();
        user2.setId(2L);
        user2.setName("Maria Santos");
        user2.setEmail("maria@email.com");
        user2.setPassword("senha456");
        user2.setCreatedAt(LocalDateTime.now());

        userRequestDTO = new UserRequestDTO(
            "João Silva",
            "joao@email.com",
            "senha123"
        );

        userResponseDTO = UserMapper.toDTO(user1);
    }

    @Test
    @DisplayName("Deve retornar todos os usuários")
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("João Silva")))
                .andExpect(jsonPath("$[0].email", is("joao@email.com")))
                .andExpect(jsonPath("$[1].name", is("Maria Santos")))
                .andExpect(jsonPath("$[1].email", is("maria@email.com")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Deve retornar usuário por ID quando existir")
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));

        // Act & Assert
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.email", is("joao@email.com")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando usuário não existir por ID")
    void getUserById_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @DisplayName("Deve retornar usuário por email quando existir")
    void getUserByEmail_WhenUserExists_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.findByEmail("joao@email.com")).thenReturn(Optional.of(user1));

        // Act & Assert
        mockMvc.perform(get("/api/users/email/joao@email.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.email", is("joao@email.com")));

        verify(userService, times(1)).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve retornar 404 quando usuário não existir por email")
    void getUserByEmail_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(userService.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/email/naoexiste@email.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findByEmail("naoexiste@email.com");
    }

    @Test
    @DisplayName("Deve criar novo usuário com sucesso")
    void createUser_WithValidData_ShouldCreateUser() throws Exception {
        // Arrange
        when(userService.findByEmail("joao@email.com")).thenReturn(Optional.empty());
        when(userService.createUser(any(User.class))).thenReturn(user1);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.email", is("joao@email.com")));

        verify(userService, times(1)).findByEmail("joao@email.com");
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar usuário com email duplicado")
    void createUser_WithDuplicateEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(userService.findByEmail("joao@email.com")).thenReturn(Optional.of(user1));

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email já está em uso"));

        verify(userService, times(1)).findByEmail("joao@email.com");
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar usuário com dados inválidos")
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO invalidDTO = new UserRequestDTO("", "email-invalido", "123");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário existente")
    void updateUser_WhenUserExists_ShouldUpdateUser() throws Exception {
        // Arrange
        UserRequestDTO updateDTO = new UserRequestDTO(
            "João Silva Atualizado",
            "joao@email.com",
            "novaSenha123"
        );

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("João Silva Atualizado");
        updatedUser.setEmail("joao@email.com");
        updatedUser.setPassword("novaSenha123");
        updatedUser.setCreatedAt(user1.getCreatedAt());

        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userService.createUser(any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("João Silva Atualizado")));

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar usuário inexistente")
    void updateUser_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar usuário com email já existente")
    void updateUser_WithDuplicateEmail_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO updateDTO = new UserRequestDTO(
            "João Silva",
            "maria@email.com", // Email já existe para outro usuário
            "senha123"
        );

        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userService.findByEmail("maria@email.com")).thenReturn(Optional.of(user2));

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email já está em uso por outro usuário"));

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).findByEmail("maria@email.com");
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar usuário existente")
    void deleteUser_WhenUserExists_ShouldDeleteUser() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar usuário inexistente")
    void deleteUser_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
        verify(userService, never()).deleteUser(anyLong());
    }
}
