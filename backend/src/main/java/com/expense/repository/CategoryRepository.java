package com.expense.repository;

import com.expense.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Busca categoria por nome exato
     */
    Optional<Category> findByName(String name);
    
    /**
     * Busca categorias com nome contendo (case insensitive)
     */
    List<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * Verifica se existe categoria com o nome
     */
    boolean existsByName(String name);
    
    /**
     * Busca todas categorias ordenadas por nome
     */
    List<Category> findAllByOrderByNameAsc();
    
    /**
     * Busca todas categorias ordenadas por data de criação
     */
    List<Category> findAllByOrderByCreatedAtDesc();
}