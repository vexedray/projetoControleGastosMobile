package com.expense.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.UserMapper;
import com.expense.model.User;
import com.expense.service.UserService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("GET /api/users - Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> usersDTO = users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.info("Found {} users", usersDTO.size());
        return ResponseEntity.ok(usersDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Fetching user by ID", id);
        return userService.getUserById(id)
                .map(user -> {
                    logger.info("User found: {}", user.getName());
                    return ResponseEntity.ok(userMapper.toResponseDTO(user));
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        logger.info("GET /api/users/email/{} - Fetching user by email", email);
        return userService.findByEmail(email)
                .map(user -> {
                    logger.info("User found: {}", user.getName());
                    return ResponseEntity.ok(userMapper.toResponseDTO(user));
                })
                .orElseGet(() -> {
                    logger.warn("User with email {} not found", email);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("POST /api/users - Creating new user: {}", userRequestDTO.getEmail());
        
        try {
            // Validate if email already exists
            if (userService.findByEmail(userRequestDTO.getEmail()).isPresent()) {
                logger.error("Email {} is already in use", userRequestDTO.getEmail());
                return ResponseEntity.badRequest()
                    .body("Email is already in use");
            }
            
            User user = userMapper.toEntity(userRequestDTO);
            User createdUser = userService.createUser(user);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(createdUser);
            logger.info("User created with ID: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("PUT /api/users/{} - Updating user", id);
        
        try {
            return userService.getUserById(id)
                    .map(existingUser -> {
                        // Check if new email is already being used by another user
                        if (!existingUser.getEmail().equals(userRequestDTO.getEmail())) {
                            if (userService.findByEmail(userRequestDTO.getEmail()).isPresent()) {
                                logger.error("Email {} is already in use by another user", userRequestDTO.getEmail());
                                return ResponseEntity.badRequest()
                                    .body("Email is already in use by another user");
                            }
                        }
                        
                        existingUser.setName(userRequestDTO.getName());
                        existingUser.setEmail(userRequestDTO.getEmail());
                        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
                            existingUser.setPassword(userRequestDTO.getPassword());
                        }
                        
                        User updatedUser = userService.createUser(existingUser);
                        UserResponseDTO responseDTO = userMapper.toResponseDTO(updatedUser);
                        logger.info("User {} updated successfully", id);
                        return ResponseEntity.ok((Object) responseDTO);
                    })
                    .orElseGet(() -> {
                        logger.warn("User with ID {} not found for update", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Deleting user", id);
        
        return userService.getUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    logger.info("User {} deleted successfully", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found for deletion", id);
                    return ResponseEntity.notFound().build();
                });
    }
}