package com.expense.repository;

import com.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca usuário por email
     */
    User findByEmail(String email);
    
    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);
}