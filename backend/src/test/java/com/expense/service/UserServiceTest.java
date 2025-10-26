package com.expense.service;

import com.expense.model.User;
import com.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@email.com");
        testUser.setPassword("password123");
        testUser.setCreatedAt(LocalDateTime.now());
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
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findByEmail("test@email.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test User", result.get().getName());
        verify(userRepository, times(1)).findByEmail("test@email.com");
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateAndReturnUser() {
        // Arrange
        User updatedDetails = new User();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated@email.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        Optional<User> result = userService.updateUser(1L, updatedDetails);

        // Assert
        assertTrue(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
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
    void count_ShouldReturnTotalUsers() {
        // Arrange
        when(userRepository.count()).thenReturn(5L);

        // Act
        long result = userService.count();

        // Assert
        assertEquals(5L, result);
        verify(userRepository, times(1)).count();
    }

    @Test
    void isEmailUnique_WhenEmailIsUnique_ShouldReturnTrue() {
        // Arrange
        when(userRepository.findByEmail("unique@email.com")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.isEmailUnique("unique@email.com", null);

        // Assert
        assertTrue(result);
    }

    @Test
    void isEmailUnique_WhenEmailBelongsToSameUser_ShouldReturnTrue() {
        // Arrange
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.isEmailUnique("test@email.com", 1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void findByNameContaining_ShouldReturnMatchingUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByNameContainingIgnoreCase("Test")).thenReturn(users);

        // Act
        List<User> result = userService.findByNameContaining("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByNameContainingIgnoreCase("Test");
    }
}
