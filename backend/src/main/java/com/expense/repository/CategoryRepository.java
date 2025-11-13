package com.expense.repository;

import com.expense.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Busca categoria por nome exato
     */
    Optional<Category> findByName(String name);
    
    /**
     * Verifica se existe categoria com o nome
     */
    boolean existsByName(String name);
}