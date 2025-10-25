package com.expense.repository;

import com.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca usuários com nome contendo (case insensitive)
     */
    List<User> findByNameContainingIgnoreCase(String name);
    
    /**
     * Busca usuário por nome exato
     */
    Optional<User> findByName(String name);
    
    /**
     * Busca todos usuários ordenados por nome
     */
    List<User> findAllByOrderByNameAsc();
    
    /**
     * Busca todos usuários ordenados por data de criação (mais recentes primeiro)
     */
    List<User> findAllByOrderByCreatedAtDesc();
    
    /**
     * Busca usuários com email contendo
     */
    List<User> findByEmailContainingIgnoreCase(String email);
    
    /**
     * Conta usuários com nome contendo
     */
    long countByNameContainingIgnoreCase(String name);
    
    /**
     * Query customizada: busca usuários com despesas
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.expenses e")
    List<User> findUsersWithExpenses();
    
    /**
     * Query customizada: busca usuários sem despesas
     */
    @Query("SELECT u FROM User u WHERE u.expenses IS EMPTY")
    List<User> findUsersWithoutExpenses();
    
    /**
     * Query customizada: busca usuários por parte do nome ou email
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchByNameOrEmail(@Param("searchTerm") String searchTerm);
}