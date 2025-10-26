package com.expense.service;

import com.expense.model.User;
import com.expense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Find all users
     */
    public List<User> getAllUsers() {
        logger.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.debug("Found {} users", users.size());
        return users;
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.debug("User found: {}", user.get().getName());
        } else {
            logger.debug("User with ID {} not found", id);
        }
        return user;
    }
    
    /**
     * Find user by ID (alias for compatibility)
     */
    public Optional<User> findById(Long id) {
        return getUserById(id);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        logger.debug("Fetching user with email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            logger.debug("User found: {}", user.get().getName());
        } else {
            logger.debug("User with email {} not found", email);
        }
        return user;
    }
    
    /**
     * Find users by name (partial match)
     */
    public List<User> findByNameContaining(String name) {
        logger.debug("Fetching users with name containing: {}", name);
        List<User> users = userRepository.findByNameContainingIgnoreCase(name);
        logger.debug("Encontrados {} usuários", users.size());
        return users;
    }
    
    /**
     * Busca usuários ordenados por nome
     */
    public List<User> findAllOrderByName() {
        logger.debug("Buscando usuários ordenados por nome");
        return userRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Busca usuários ordenados por data de criação
     */
    public List<User> findAllOrderByCreatedAt() {
        logger.debug("Buscando usuários ordenados por data de criação");
        return userRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Cria ou atualiza um usuário
     */
    public User createUser(User user) {
        if (user.getId() == null) {
            logger.debug("Criando novo usuário: {}", user.getEmail());
        } else {
            logger.debug("Atualizando usuário ID {}: {}", user.getId(), user.getEmail());
        }
        User saved = userRepository.save(user);
        logger.debug("Usuário salvo com ID: {}", saved.getId());
        return saved;
    }
    
    /**
     * Salva ou atualiza um usuário (alias para compatibilidade)
     */
    public User save(User user) {
        return createUser(user);
    }
    
    /**
     * Atualiza dados de um usuário existente
     */
    public Optional<User> updateUser(Long id, User userDetails) {
        logger.debug("Atualizando usuário ID: {}", id);
        return userRepository.findById(id)
            .map(existingUser -> {
                existingUser.setName(userDetails.getName());
                existingUser.setEmail(userDetails.getEmail());
                if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                    existingUser.setPassword(userDetails.getPassword());
                }
                User updated = userRepository.save(existingUser);
                logger.debug("Usuário {} atualizado com sucesso", id);
                return updated;
            });
    }
    
    /**
     * Deleta usuário por ID
     */
    public void deleteUser(Long id) {
        logger.debug("Deletando usuário com ID: {}", id);
        userRepository.deleteById(id);
        logger.debug("Usuário {} deletado com sucesso", id);
    }
    
    /**
     * Verifica se usuário existe por ID
     */
    public boolean existsById(Long id) {
        logger.debug("Verificando existência do usuário com ID: {}", id);
        boolean exists = userRepository.existsById(id);
        logger.debug("Usuário {} existe: {}", id, exists);
        return exists;
    }
    
    /**
     * Verifica se usuário existe por email
     */
    public boolean existsByEmail(String email) {
        logger.debug("Verificando existência do usuário com email: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        logger.debug("Usuário com email '{}' existe: {}", email, exists);
        return exists;
    }
    
    /**
     * Conta total de usuários
     */
    public long count() {
        logger.debug("Contando total de usuários");
        long total = userRepository.count();
        logger.debug("Total de usuários: {}", total);
        return total;
    }
    
    /**
     * Valida se email é único (exceto para o próprio usuário)
     */
    public boolean isEmailUnique(String email, Long userId) {
        logger.debug("Validando se email {} é único (exceto para usuário {})", email, userId);
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isEmpty()) {
            logger.debug("Email {} é único", email);
            return true;
        }
        
        boolean isUnique = existingUser.get().getId().equals(userId);
        logger.debug("Email {} é único para usuário {}: {}", email, userId, isUnique);
        return isUnique;
    }
    
    /**
     * Valida credenciais de login (sem criptografia - apenas exemplo)
     * NOTA: Em produção, use Spring Security com BCrypt
     */
    public Optional<User> validateLogin(String email, String password) {
        logger.debug("Validando login para email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            logger.debug("Login válido para: {}", email);
            return user;
        }
        
        logger.debug("Login inválido para: {}", email);
        return Optional.empty();
    }
}