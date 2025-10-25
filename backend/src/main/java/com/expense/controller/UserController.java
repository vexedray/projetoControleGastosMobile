package com.expense.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expense.model.User;
import com.expense.service.UserService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("GET /api/users - Buscando todos os usuários");
        List<User> users = userService.getAllUsers();
        logger.info("Encontrados {} usuários", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Buscando usuário por ID", id);
        return userService.getUserById(id)
                .map(user -> {
                    logger.info("Usuário encontrado: {}", user.getName());
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("Usuário com ID {} não encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        logger.info("GET /api/users/email/{} - Buscando usuário por email", email);
        return userService.findByEmail(email)
                .map(user -> {
                    logger.info("Usuário encontrado: {}", user.getName());
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("Usuário com email {} não encontrado", email);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        logger.info("POST /api/users - Criando novo usuário: {}", user.getEmail());
        
        try {
            // Validar se email já existe
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                logger.error("Email {} já está em uso", user.getEmail());
                return ResponseEntity.badRequest()
                    .body("Email já está em uso");
            }
            
            User createdUser = userService.createUser(user);
            logger.info("Usuário criado com ID: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            
        } catch (Exception e) {
            logger.error("Erro ao criar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        logger.info("PUT /api/users/{} - Atualizando usuário", id);
        
        try {
            return userService.getUserById(id)
                    .map(existingUser -> {
                        // Verificar se o novo email já está sendo usado por outro usuário
                        if (!existingUser.getEmail().equals(user.getEmail())) {
                            if (userService.findByEmail(user.getEmail()).isPresent()) {
                                logger.error("Email {} já está em uso por outro usuário", user.getEmail());
                                return ResponseEntity.badRequest()
                                    .body("Email já está em uso por outro usuário");
                            }
                        }
                        
                        existingUser.setName(user.getName());
                        existingUser.setEmail(user.getEmail());
                        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                            existingUser.setPassword(user.getPassword());
                        }
                        
                        User updatedUser = userService.createUser(existingUser);
                        logger.info("Usuário {} atualizado com sucesso", id);
                        return ResponseEntity.ok((Object) updatedUser);
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