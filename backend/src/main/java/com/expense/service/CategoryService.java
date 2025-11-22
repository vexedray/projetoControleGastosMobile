package com.expense.service;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.model.Category;
import com.expense.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> findAll() {
        logger.debug("Buscando todas as categorias");
        List<Category> categories = categoryRepository.findAll();
        logger.debug("Encontradas {} categorias", categories.size());
        return categories;
    }
    
    public Optional<Category> findById(Long id) {
        logger.debug("Buscando categoria com ID: {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            logger.debug("Categoria encontrada: {}", category.get().getName());
        } else {
            logger.debug("Categoria com ID {} não encontrada", id);
        }
        return category;
    }
    
    public Optional<Category> findByName(String name) {
        logger.debug("Buscando categoria com nome: {}", name);
        Category category = categoryRepository.findByName(name);
        return Optional.ofNullable(category);
    }
    
    public Category createCategory(CategoryRequestDTO categoryDTO) {
        logger.debug("Criando nova categoria: {}", categoryDTO.getName());
        
        if (categoryRepository.findByName(categoryDTO.getName()) != null) {
            logger.error("Categoria já existe: {}", categoryDTO.getName());
            throw new RuntimeException("Categoria já existe");
        }
        
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setColor(categoryDTO.getColor());
        category.setIcon(categoryDTO.getIcon());
        category.setCreatedAt(LocalDateTime.now());
        
        Category saved = categoryRepository.save(category);
        logger.debug("Categoria criada com ID: {}", saved.getId());
        return saved;
    }
    
    public Category save(Category category) {
        if (category.getId() == null) {
            logger.debug("Criando nova categoria: {}", category.getName());
            if (category.getCreatedAt() == null) {
                category.setCreatedAt(LocalDateTime.now());
            }
        } else {
            logger.debug("Atualizando categoria ID {}: {}", category.getId(), category.getName());
        }
        Category saved = categoryRepository.save(category);
        logger.debug("Categoria salva com ID: {}", saved.getId());
        return saved;
    }
    
    public Category updateCategory(Long id, CategoryRequestDTO categoryDTO) {
        logger.debug("Atualizando categoria ID: {}", id);
        
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Categoria não encontrada: {}", id);
                return new RuntimeException("Categoria não encontrada");
            });
        
        if (!category.getName().equals(categoryDTO.getName())) {
            if (categoryRepository.findByName(categoryDTO.getName()) != null) {
                logger.error("Nome de categoria já existe: {}", categoryDTO.getName());
                throw new RuntimeException("Nome de categoria já existe");
            }
            category.setName(categoryDTO.getName());
        }
        
        category.setDescription(categoryDTO.getDescription());
        category.setColor(categoryDTO.getColor());
        category.setIcon(categoryDTO.getIcon());
        
        Category saved = categoryRepository.save(category);
        logger.debug("Categoria {} atualizada com sucesso", id);
        return saved;
    }
    
    public void deleteById(Long id) {
        logger.debug("Deletando categoria com ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            logger.error("Categoria não encontrada para exclusão: {}", id);
            throw new RuntimeException("Categoria não encontrada");
        }
        categoryRepository.deleteById(id);
        logger.debug("Categoria {} deletada com sucesso", id);
    }
    
    public boolean existsById(Long id) {
        logger.debug("Verificando existência da categoria com ID: {}", id);
        boolean exists = categoryRepository.existsById(id);
        logger.debug("Categoria {} existe: {}", id, exists);
        return exists;
    }
    
    public boolean existsByName(String name) {
        logger.debug("Verificando existência da categoria com nome: {}", name);
        boolean exists = categoryRepository.findByName(name) != null;
        logger.debug("Categoria '{}' existe: {}", name, exists);
        return exists;
    }
    
    public long count() {
        logger.debug("Contando total de categorias");
        long total = categoryRepository.count();
        logger.debug("Total de categorias: {}", total);
        return total;
    }
}