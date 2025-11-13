package com.expense.service;

import com.expense.model.User;
import com.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setPassword("senha123");
        user.setCreatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Maria Santos");
        user2.setEmail("maria@email.com");
        user2.setPassword("senha456");
        user2.setCreatedAt(LocalDateTime.now());

        userList = Arrays.asList(user, user2);
    }

    @Test
    @DisplayName("Deve retornar todos os usuários")
    void shouldReturnAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals("João Silva", result.get(0).getName());
        assertEquals("Maria Santos", result.get(1).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve encontrar usuário por ID")
    void shouldFindUserById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("João Silva", result.get().getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando usuário não existe")
    void shouldReturnEmptyWhenUserNotFound() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve encontrar usuário por email")
    void shouldFindUserByEmail() {
        // Given
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByEmail("joao@email.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("joao@email.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve encontrar usuários por nome contendo")
    void shouldFindUsersByNameContaining() {
        // Given
        when(userRepository.findByNameContainingIgnoreCase("joão")).thenReturn(Arrays.asList(user));

        // When
        List<User> result = userService.findByNameContaining("joão");

        // Then
        assertEquals(1, result.size());
        assertEquals("João Silva", result.get(0).getName());
        verify(userRepository, times(1)).findByNameContainingIgnoreCase("joão");
    }

    @Test
    @DisplayName("Deve criar novo usuário")
    void shouldCreateNewUser() {
        // Given
        User newUser = new User();
        newUser.setName("Pedro Costa");
        newUser.setEmail("pedro@email.com");
        newUser.setPassword("senha789");

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setName("Pedro Costa");
        savedUser.setEmail("pedro@email.com");
        savedUser.setPassword("senha789");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result.getId());
        assertEquals("Pedro Costa", result.getName());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    @DisplayName("Deve atualizar usuário existente")
    void shouldUpdateExistingUser() {
        // Given
        User userDetails = new User();
        userDetails.setName("João Silva Atualizado");
        userDetails.setEmail("joao.novo@email.com");
        userDetails.setPassword("novaSenha123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        Optional<User> result = userService.updateUser(1L, userDetails);

        // Then
        assertTrue(result.isPresent());
        assertEquals("João Silva Atualizado", user.getName());
        assertEquals("joao.novo@email.com", user.getEmail());
        assertEquals("novaSenha123", user.getPassword());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Deve atualizar usuário sem alterar senha quando senha está vazia")
    void shouldUpdateUserWithoutChangingPasswordWhenPasswordIsEmpty() {
        // Given
        User userDetails = new User();
        userDetails.setName("João Silva Atualizado");
        userDetails.setEmail("joao.novo@email.com");
        userDetails.setPassword(""); // senha vazia

        String originalPassword = user.getPassword();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        Optional<User> result = userService.updateUser(1L, userDetails);

        // Then
        assertTrue(result.isPresent());
        assertEquals(originalPassword, user.getPassword()); // senha não deve mudar
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar usuário inexistente")
    void shouldReturnEmptyWhenUpdatingNonExistentUser() {
        // Given
        User userDetails = new User();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.updateUser(99L, userDetails);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar usuário por ID")
    void shouldDeleteUserById() {
        // Given
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve verificar se usuário existe por ID")
    void shouldCheckIfUserExistsById() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        boolean exists = userService.existsById(1L);

        // Then
        assertTrue(exists);
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Deve verificar se usuário existe por email")
    void shouldCheckIfUserExistsByEmail() {
        // Given
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        // When
        boolean exists = userService.existsByEmail("joao@email.com");

        // Then
        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve validar email único para usuário diferente")
    void shouldValidateEmailIsUniqueForDifferentUser() {
        // Given
        when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());

        // When
        boolean isUnique = userService.isEmailUnique("novo@email.com", 1L);

        // Then
        assertTrue(isUnique);
        verify(userRepository, times(1)).findByEmail("novo@email.com");
    }

    @Test
    @DisplayName("Deve validar email único para o mesmo usuário")
    void shouldValidateEmailIsUniqueForSameUser() {
        // Given
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

        // When
        boolean isUnique = userService.isEmailUnique("joao@email.com", 1L);

        // Then
        assertTrue(isUnique);
        verify(userRepository, times(1)).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve retornar false quando email não é único")
    void shouldReturnFalseWhenEmailIsNotUnique() {
        // Given
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("joao@email.com");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(otherUser));

        // When
        boolean isUnique = userService.isEmailUnique("joao@email.com", 1L);

        // Then
        assertFalse(isUnique);
        verify(userRepository, times(1)).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve validar credenciais de login válidas")
    void shouldValidateValidLogin() {
        // Given
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.validateLogin("joao@email.com", "senha123");

        // Then
        assertTrue(result.isPresent());
        assertEquals("João Silva", result.get().getName());
        verify(userRepository, times(1)).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para credenciais inválidas")
    void shouldReturnEmptyForInvalidLogin() {
        // Given
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.validateLogin("joao@email.com", "senhaErrada");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve retornar usuários ordenados por nome")
    void shouldReturnUsersOrderedByName() {
        // Given
        when(userRepository.findAllByOrderByNameAsc()).thenReturn(userList);

        // When
        List<User> result = userService.findAllOrderByName();

        // Then
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("Deve contar total de usuários")
    void shouldCountTotalUsers() {
        // Given
        when(userRepository.count()).thenReturn(2L);

        // When
        long count = userService.count();

        // Then
        assertEquals(2L, count);
        verify(userRepository, times(1)).count();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void shouldReturnEmptyListWhenNoUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve usar método alias findById corretamente")
    void shouldUseFindByIdAlias() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("João Silva", result.get().getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve usar método alias save corretamente")
    void shouldUseSaveAlias() {
        // Given
        when(userRepository.save(user)).thenReturn(user);

        // When
        User result = userService.save(user);

        // Then
        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);
    }
}