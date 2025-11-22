package com.expense.service;

import com.expense.model.Category;
import com.expense.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setDescription("Food and beverages");
    }

    @Test
    void testFindAll_ShouldReturnAllCategories() {
        // Arrange
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Transport");
        category2.setDescription("Transportation expenses");
        
        List<Category> categories = Arrays.asList(category, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getName());
        assertEquals("Transport", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testFindById_WhenCategoryExists_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getName());
        assertEquals("Food and beverages", result.get().getDescription());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_WhenCategoryDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void testSave_ShouldSaveAndReturnCategory() {
        // Arrange
        Category newCategory = new Category();
        newCategory.setName("Entertainment");
        newCategory.setDescription("Entertainment expenses");

        Category savedCategory = new Category();
        savedCategory.setId(3L);
        savedCategory.setName("Entertainment");
        savedCategory.setDescription("Entertainment expenses");

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        Category result = categoryService.save(newCategory);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Entertainment", result.getName());
        assertEquals("Entertainment expenses", result.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteById_ShouldCallRepositoryDelete() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        // Act
        categoryService.deleteById(1L);

        // Assert
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAll_WhenNoCategoriesExist_ShouldReturnEmptyList() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCategory_ShouldUpdateAndReturnCategory() {
        // Arrange
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Food & Drinks");
        updatedCategory.setDescription("Updated description");

        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.save(updatedCategory);

        // Assert
        assertNotNull(result);
        assertEquals("Food & Drinks", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}