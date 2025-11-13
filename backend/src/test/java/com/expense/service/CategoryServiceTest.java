package com.expense.service;

import com.expense.model.Category;
import com.expense.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Alimentação");
        category.setDescription("Gastos com comida");
        category.setCreatedAt(LocalDateTime.now());

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Transporte");
        category2.setDescription("Gastos com transporte");
        category2.setCreatedAt(LocalDateTime.now());

        categoryList = Arrays.asList(category, category2);
    }

    @Test
    @DisplayName("Deve retornar todas as categorias")
    void shouldReturnAllCategories() {
        // Given
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // When
        List<Category> result = categoryService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals("Alimentação", result.get(0).getName());
        assertEquals("Transporte", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve encontrar categoria por ID")
    void shouldFindCategoryById() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // When
        Optional<Category> result = categoryService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Alimentação", result.get().getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando categoria não existe")
    void shouldReturnEmptyWhenCategoryNotFound() {
        // Given
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Category> result = categoryService.findById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve encontrar categoria por nome")
    void shouldFindCategoryByName() {
        // Given
        when(categoryRepository.findByName("Alimentação")).thenReturn(Optional.of(category));

        // When
        Optional<Category> result = categoryService.findByName("Alimentação");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Alimentação", result.get().getName());
        verify(categoryRepository, times(1)).findByName("Alimentação");
    }

    @Test
    @DisplayName("Deve encontrar categorias por nome contendo")
    void shouldFindCategoriesByNameContaining() {
        // Given
        when(categoryRepository.findByNameContainingIgnoreCase("trans")).thenReturn(Arrays.asList(category));

        // When
        List<Category> result = categoryService.findByNameContaining("trans");

        // Then
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase("trans");
    }

    @Test
    @DisplayName("Deve salvar nova categoria")
    void shouldSaveNewCategory() {
        // Given
        Category newCategory = new Category();
        newCategory.setName("Lazer");
        newCategory.setDescription("Gastos com entretenimento");

        Category savedCategory = new Category();
        savedCategory.setId(3L);
        savedCategory.setName("Lazer");
        savedCategory.setDescription("Gastos com entretenimento");

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When
        Category result = categoryService.save(newCategory);

        // Then
        assertNotNull(result.getId());
        assertEquals("Lazer", result.getName());
        verify(categoryRepository, times(1)).save(newCategory);
    }

    @Test
    @DisplayName("Deve atualizar categoria existente")
    void shouldUpdateExistingCategory() {
        // Given
        category.setName("Alimentação Atualizada");
        when(categoryRepository.save(category)).thenReturn(category);

        // When
        Category result = categoryService.save(category);

        // Then
        assertEquals("Alimentação Atualizada", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Deve deletar categoria por ID")
    void shouldDeleteCategoryById() {
        // Given
        doNothing().when(categoryRepository).deleteById(1L);

        // When
        categoryService.deleteById(1L);

        // Then
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve verificar se categoria existe por ID")
    void shouldCheckIfCategoryExistsById() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // When
        boolean exists = categoryService.existsById(1L);

        // Then
        assertTrue(exists);
        verify(categoryRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Deve verificar se categoria existe por nome")
    void shouldCheckIfCategoryExistsByName() {
        // Given
        when(categoryRepository.existsByName("Alimentação")).thenReturn(true);

        // When
        boolean exists = categoryService.existsByName("Alimentação");

        // Then
        assertTrue(exists);
        verify(categoryRepository, times(1)).existsByName("Alimentação");
    }

    @Test
    @DisplayName("Deve contar total de categorias")
    void shouldCountTotalCategories() {
        // Given
        when(categoryRepository.count()).thenReturn(2L);

        // When
        long count = categoryService.count();

        // Then
        assertEquals(2L, count);
        verify(categoryRepository, times(1)).count();
    }

    @Test
    @DisplayName("Deve retornar categorias ordenadas por nome")
    void shouldReturnCategoriesOrderedByName() {
        // Given
        when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(categoryList);

        // When
        List<Category> result = categoryService.findAllOrderByName();

        // Then
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há categorias")
    void shouldReturnEmptyListWhenNoCategories() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Category> result = categoryService.findAll();

        // Then
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar false quando categoria não existe por nome")
    void shouldReturnFalseWhenCategoryDoesNotExistByName() {
        // Given
        when(categoryRepository.existsByName("Inexistente")).thenReturn(false);

        // When
        boolean exists = categoryService.existsByName("Inexistente");

        // Then
        assertFalse(exists);
        verify(categoryRepository, times(1)).existsByName("Inexistente");
    }
}