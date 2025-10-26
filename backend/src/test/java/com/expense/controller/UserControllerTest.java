package com.expense.controller;

import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.UserMapper;
import com.expense.model.User;
import com.expense.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private User user;
    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encoded_password");

        requestDTO = new UserRequestDTO();
        requestDTO.setName("John Doe");
        requestDTO.setEmail("john@example.com");
        requestDTO.setPassword("password123");

        responseDTO = new UserResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("John Doe");
        responseDTO.setEmail("john@example.com");
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(user);

        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(user);
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserRequestDTO invalidDTO = new UserRequestDTO();
        invalidDTO.setName("");
        invalidDTO.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).updateUser(anyLong(), any(User.class));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(userService, never()).updateUser(anyLong(), any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    void checkEmailAvailability_WhenEmailAvailable_ShouldReturnTrue() throws Exception {
        // Arrange
        when(userService.existsByEmail("newemail@example.com")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/users/check-email/newemail@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));

        verify(userService, times(1)).existsByEmail("newemail@example.com");
    }

    @Test
    void checkEmailAvailability_WhenEmailTaken_ShouldReturnFalse() throws Exception {
        // Arrange
        when(userService.existsByEmail("john@example.com")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/users/check-email/john@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        verify(userService, times(1)).existsByEmail("john@example.com");
    }
}
