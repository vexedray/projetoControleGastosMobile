package com.expense.service;

import com.expense.dto.request.LoginRequestDTO;
import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.LoginResponseDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.UserMapper;
import com.expense.model.User;
import com.expense.repository.UserRepository;
import com.expense.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;
    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@email.com");
        testUser.setPassword("encodedPassword");
        testUser.setCreatedAt(LocalDateTime.now());

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Test User");
        userRequestDTO.setEmail("test@email.com");
        userRequestDTO.setPassword("password123");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("Test User");
        userResponseDTO.setEmail("test@email.com");

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("test@email.com");
        loginRequestDTO.setPassword("password123");
    }

    @Test
    void register_WhenEmailIsAvailable_ShouldRegisterUser() {
        // Arrange
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userMapper.toEntity(userRequestDTO)).thenReturn(testUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDTO(testUser)).thenReturn(userResponseDTO);

        // Act
        UserResponseDTO result = authService.register(userRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
        verify(userRepository, times(1)).existsByEmail("test@email.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(userRequestDTO);
        });

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("test@email.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WhenCredentialsAreValid_ShouldReturnToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDTO(testUser)).thenReturn(userResponseDTO);

        // Act
        LoginResponseDTO result = authService.login(loginRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("Bearer", result.getType());
        assertNotNull(result.getUser());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
    }

    @Test
    void login_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequestDTO);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void isEmailAvailable_WhenEmailDoesNotExist_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);

        // Act
        boolean result = authService.isEmailAvailable("new@email.com");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail("new@email.com");
    }

    @Test
    void isEmailAvailable_WhenEmailExists_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        // Act
        boolean result = authService.isEmailAvailable("test@email.com");

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail("test@email.com");
    }
}
