package com.expense.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expense.assembler.UserModelAssembler;
import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.UserMapper;
import com.expense.model.User;
import com.expense.model.hateoas.UserModel;
import com.expense.service.UserService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserModelAssembler userModelAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<UserModel>> getAllUsers() {
        logger.info("GET /api/users - Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> usersDTO = users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        // Converte para HATEOAS models
        CollectionModel<UserModel> userModels = CollectionModel.of(
            usersDTO.stream()
                .map(userModelAssembler::toModel)
                .collect(Collectors.toList())
        );
        
        // Adiciona link para a própria coleção
        userModels.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        
        logger.info("Found {} users", usersDTO.size());
        return ResponseEntity.ok(userModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Fetching user by ID", id);
        return userService.getUserById(id)
                .map(user -> {
                    logger.info("User found: {}", user.getName());
                    UserResponseDTO dto = userMapper.toResponseDTO(user);
                    UserModel model = userModelAssembler.toModel(dto);
                    return ResponseEntity.ok((Object) model);
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuário não encontrado"));
                });
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        logger.info("GET /api/users/email/{} - Fetching user by email", email);
        return userService.findByEmail(email)
                .map(user -> {
                    logger.info("User found: {}", user.getName());
                    UserResponseDTO dto = userMapper.toResponseDTO(user);
                    UserModel model = userModelAssembler.toModel(dto);
                    return ResponseEntity.ok((Object) model);
                })
                .orElseGet(() -> {
                    logger.warn("User with email {} not found", email);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuário não encontrado"));
                });
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("POST /api/users - Creating new user: {}", userRequestDTO.getEmail());
        
        try {
            // Validate if email already exists
            if (userService.existsByEmail(userRequestDTO.getEmail())) {
                logger.error("Email {} is already in use", userRequestDTO.getEmail());
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email já está em uso"));
            }
            
            User user = userMapper.toEntity(userRequestDTO);
            User createdUser = userService.createUser(user);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(createdUser);
            UserModel model = userModelAssembler.toModel(responseDTO);
            
            logger.info("User created with ID: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
            
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
                            if (userService.existsByEmail(userRequestDTO.getEmail())) {
                                logger.error("Email {} is already in use by another user", userRequestDTO.getEmail());
                                return ResponseEntity.badRequest()
                                    .body(Map.of("error", "Email já está em uso por outro usuário"));
                            }
                        }
                        
                        existingUser.setName(userRequestDTO.getName());
                        existingUser.setEmail(userRequestDTO.getEmail());
                        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
                            existingUser.setPassword(userRequestDTO.getPassword());
                        }
                        
                        User updatedUser = userService.createUser(existingUser);
                        UserResponseDTO responseDTO = userMapper.toResponseDTO(updatedUser);
                        UserModel model = userModelAssembler.toModel(responseDTO);
                        
                        logger.info("User {} updated successfully", id);
                        return ResponseEntity.ok((Object) model);
                    })
                    .orElseGet(() -> {
                        logger.warn("User with ID {} not found for update", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Usuário não encontrado"));
                    });
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Deleting user", id);
        
        return userService.getUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    logger.info("User {} deleted successfully", id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found for deletion", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuário não encontrado"));
                });
    }
    
    @GetMapping("/check-email/{email}")
    public ResponseEntity<java.util.Map<String, Boolean>> checkEmailAvailability(@PathVariable String email) {
        logger.info("GET /api/users/check-email/{} - Checking email availability", email);
        boolean isAvailable = !userService.existsByEmail(email);
        logger.info("Email {} is available: {}", email, isAvailable);
        return ResponseEntity.ok(java.util.Map.of("available", isAvailable));
    }
}