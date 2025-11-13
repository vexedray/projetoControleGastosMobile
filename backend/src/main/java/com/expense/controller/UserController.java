package com.expense.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expense.model.User;
import com.expense.service.UserService;
import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.mapper.UserMapper;

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

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("GET /api/users - Buscando todos os usuários");
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userDTOs = users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Encontrados {} usuários", userDTOs.size());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Buscando usuário por ID", id);
        return userService.getUserById(id)
                .map(user -> {
                    UserResponseDTO userDTO = UserMapper.toDTO(user);
                    logger.info("Usuário encontrado: {}", user.getName());
                    return ResponseEntity.ok(userDTO);
                })
                .orElseGet(() -> {
                    logger.warn("Usuário com ID {} não encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        logger.info("GET /api/users/email/{} - Buscando usuário por email", email);
        return userService.findByEmail(email)
                .map(user -> {
                    UserResponseDTO userDTO = UserMapper.toDTO(user);
                    logger.info("Usuário encontrado: {}", user.getName());
                    return ResponseEntity.ok(userDTO);
                })
                .orElseGet(() -> {
                    logger.warn("Usuário com email {} não encontrado", email);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("POST /api/users - Criando novo usuário: {}", userRequestDTO.email());
        
        try {
            // Validar se email já existe
            if (userService.findByEmail(userRequestDTO.email()).isPresent()) {
                logger.error("Email {} já está em uso", userRequestDTO.email());
                return ResponseEntity.badRequest()
                    .body("Email já está em uso");
            }
            
            User user = UserMapper.toEntity(userRequestDTO);
            User createdUser = userService.createUser(user);
            UserResponseDTO responseDTO = UserMapper.toDTO(createdUser);
            
            logger.info("Usuário criado com ID: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            
        } catch (Exception e) {
            logger.error("Erro ao criar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("PUT /api/users/{} - Atualizando usuário", id);
        
        try {
            return userService.getUserById(id)
                    .map(existingUser -> {
                        // Verificar se o novo email já está sendo usado por outro usuário
                        if (!existingUser.getEmail().equals(userRequestDTO.email())) {
                            if (userService.findByEmail(userRequestDTO.email()).isPresent()) {
                                logger.error("Email {} já está em uso por outro usuário", userRequestDTO.email());
                                return ResponseEntity.badRequest()
                                    .body("Email já está em uso por outro usuário");
                            }
                        }
                        
                        UserMapper.updateEntity(existingUser, userRequestDTO);
                        User updatedUser = userService.createUser(existingUser);
                        UserResponseDTO responseDTO = UserMapper.toDTO(updatedUser);
                        
                        logger.info("Usuário {} atualizado com sucesso", id);
                        return ResponseEntity.ok((Object) responseDTO);
                    })
                    .orElseGet(() -> {
                        logger.warn("Usuário com ID {} não encontrado para atualização", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Deletando usuário", id);
        
        return userService.getUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    logger.info("Usuário {} deletado com sucesso", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Usuário com ID {} não encontrado para deleção", id);
                    return ResponseEntity.notFound().build();
                });
    }
}