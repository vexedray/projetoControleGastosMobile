package com.expense.service;

import com.expense.model.Category;
import com.expense.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Busca todas as categorias
     */
    public List<Category> findAll() {
        logger.debug("Buscando todas as categorias");
        List<Category> categories = categoryRepository.findAll();
        logger.debug("Encontradas {} categorias", categories.size());
        return categories;
    }
    
    /**
     * Busca categoria por ID
     */
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
    
    /**
     * Busca categoria por nome
     */
    public Optional<Category> findByName(String name) {
        logger.debug("Buscando categoria com nome: {}", name);
        return categoryRepository.findByName(name);
    }
    
    /**
     * Busca categorias por nome (parcial)
     */
    public List<Category> findByNameContaining(String name) {
        logger.debug("Buscando categorias com nome contendo: {}", name);
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Salva ou atualiza uma categoria
     */
    public Category save(Category category) {
        if (category.getId() == null) {
            logger.debug("Criando nova categoria: {}", category.getName());
        } else {
            logger.debug("Atualizando categoria ID {}: {}", category.getId(), category.getName());
        }
        Category saved = categoryRepository.save(category);
        logger.debug("Categoria salva com ID: {}", saved.getId());
        return saved;
    }
    
    /**
     * Deleta categoria por ID
     */
    public void deleteById(Long id) {
        logger.debug("Deletando categoria com ID: {}", id);
        categoryRepository.deleteById(id);
        logger.debug("Categoria {} deletada com sucesso", id);
    }
    
    /**
     * Verifica se categoria existe por ID
     */
    public boolean existsById(Long id) {
        logger.debug("Verificando existência da categoria com ID: {}", id);
        boolean exists = categoryRepository.existsById(id);
        logger.debug("Categoria {} existe: {}", id, exists);
        return exists;
    }
    
    /**
     * Verifica se categoria existe por nome
     */
    public boolean existsByName(String name) {
        logger.debug("Verificando existência da categoria com nome: {}", name);
        boolean exists = categoryRepository.existsByName(name);
        logger.debug("Categoria '{}' existe: {}", name, exists);
        return exists;
    }
    
    /**
     * Conta total de categorias
     */
    public long count() {
        logger.debug("Contando total de categorias");
        long total = categoryRepository.count();
        logger.debug("Total de categorias: {}", total);
        return total;
    }
    
    /**
     * Busca categorias ordenadas por nome
     */
    public List<Category> findAllOrderByName() {
        logger.debug("Buscando categorias ordenadas por nome");
        return categoryRepository.findAllByOrderByNameAsc();
    }
}