package com.expense.repository;

import com.expense.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find category by name
     */
    Category findByName(String name);
    
    /**
     * Check if category exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find all categories for a specific user
     */
    List<Category> findByUserId(Long userId);
    
    /**
     * Find category by ID and user ID (for security)
     */
    Optional<Category> findByIdAndUserId(Long id, Long userId);
}