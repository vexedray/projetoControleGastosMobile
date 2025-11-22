package com.expense.service;

import com.expense.dto.request.UserRequestDTO;
import com.expense.model.User;
import com.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@email.com");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setCreatedAt(LocalDateTime.now());

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Test User");
        userRequestDTO.setEmail("test@email.com");
        userRequestDTO.setPassword("password123");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("test@email.com")).thenReturn(testUser);

        // Act
        Optional<User> result = userService.findByEmail("test@email.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
        verify(userRepository, times(1)).findByEmail("test@email.com");
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(null);

        // Act
        Optional<User> result = userService.findByEmail("notfound@email.com");

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("notfound@email.com");
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail("test@email.com");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("test@email.com");
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByEmail("notfound@email.com")).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail("notfound@email.com");

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail("notfound@email.com");
    }

    @Test
    void createUser_WithValidData_ShouldCreateAndReturnUser() {
        // Arrange
        when(userRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(userRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail(userRequestDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(userRequestDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(testUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(userRequestDTO);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(userRequestDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_WithEntity_ShouldEncodePasswordAndSave() {
        // Arrange
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@email.com");
        newUser.setPassword("plainPassword");

        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void createUser_WithAlreadyEncodedPassword_ShouldNotEncodeAgain() {
        // Arrange
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("new@email.com");
        newUser.setPassword("$2a$10$alreadyEncoded");

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateAndReturnUser() {
        // Arrange
        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setEmail("test@email.com");
        updateDTO.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode(updateDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithDifferentEmail_WhenEmailAvailable_ShouldUpdate() {
        // Arrange
        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setEmail("newemail@email.com");
        updateDTO.setPassword("");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("newemail@email.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("newemail@email.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithDifferentEmail_WhenEmailTaken_ShouldThrowException() {
        // Arrange
        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setEmail("taken@email.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("taken@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("taken@email.com")).thenReturn(anotherUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, updateDTO);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("taken@email.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, userRequestDTO);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999L);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}