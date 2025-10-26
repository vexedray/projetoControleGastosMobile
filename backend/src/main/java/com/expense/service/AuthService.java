package com.expense.service;

import com.expense.dto.request.LoginRequestDTO;
import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.LoginResponseDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.UserMapper;
import com.expense.model.User;
import com.expense.repository.UserRepository;
import com.expense.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    /**
     * Registers a new user
     */
    public UserResponseDTO register(UserRequestDTO requestDTO) {
        logger.debug("Registering new user: {}", requestDTO.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            logger.warn("Email already registered: {}", requestDTO.getEmail());
            throw new RuntimeException("Email already registered");
        }
        
        // Convert DTO to entity
        User user = userMapper.toEntity(requestDTO);
        
        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save user
        User savedUser = userRepository.save(user);
        logger.debug("User registered successfully: ID {}", savedUser.getId());
        
        // Return response DTO
        return userMapper.toResponseDTO(savedUser);
    }
    
    /**
     * Authenticates user and returns JWT token
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        logger.debug("Login attempt for: {}", loginRequest.getEmail());
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );
        
        // Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Find user
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        logger.debug("Login successful for: {}", loginRequest.getEmail());
        
        // Return response with token and user data
        return new LoginResponseDTO(token, userMapper.toResponseDTO(user));
    }
    
    /**
     * Validates if email is available
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}

