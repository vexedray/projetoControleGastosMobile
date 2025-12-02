package com.expense.service;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.repository.CategoryRepository;
import com.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private User user;
    private CategoryRequestDTO categoryRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setDescription("Food and beverages");
        category.setColor("#FF5733");
        category.setIcon("🍔");
        category.setUser(user);
        category.setCreatedAt(LocalDateTime.now());

        categoryRequestDTO = new CategoryRequestDTO();
        categoryRequestDTO.setName("Food");
        categoryRequestDTO.setDescription("Food and beverages");
        categoryRequestDTO.setColor("#FF5733");
        categoryRequestDTO.setIcon("🍔");
    }

    @Test
    void getAllCategories_ShouldReturnUserCategories() {
        // Arrange
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Transport");
        category2.setUser(user);
        
        List<Category> categories = Arrays.asList(category, category2);
        when(categoryRepository.findByUserId(1L)).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getAllCategories(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getName());
        assertEquals("Transport", result.get(1).getName());
        verify(categoryRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getCategoryById_WhenCategoryExistsAndBelongsToUser_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryService.getCategoryById(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getName());
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotBelongToUser_ShouldReturnEmpty() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.getCategoryById(1L, 999L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 999L);
    }

    @Test
    void createCategory_WithValidData_ShouldCreateAndReturnCategory() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.createCategory(categoryRequestDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Food", result.getName());
        assertEquals("Food and beverages", result.getDescription());
        verify(userRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(categoryRequestDTO, 999L);
        });

        verify(userRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenCategoryExists_ShouldUpdateAndReturn() {
        // Arrange
        CategoryRequestDTO updateDTO = new CategoryRequestDTO();
        updateDTO.setName("Food & Drinks");
        updateDTO.setDescription("Updated description");
        updateDTO.setColor("#00FF00");
        updateDTO.setIcon("🍕");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Food & Drinks");
        updatedCategory.setDescription("Updated description");
        updatedCategory.setColor("#00FF00");
        updatedCategory.setIcon("🍕");
        updatedCategory.setUser(user);

        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.updateCategory(1L, updateDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Food & Drinks", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenCategoryDoesNotBelongToUser_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(1L, categoryRequestDTO, 999L);
        });

        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldDelete() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        // Act
        categoryService.deleteCategory(1L, 1L);

        // Assert
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_WhenCategoryDoesNotBelongToUser_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(1L, 999L);
        });

        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 999L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void findAll_ShouldReturnAllCategories() {
        // Arrange
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Transport");
        
        List<Category> categories = Arrays.asList(category, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenCategoryExists_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenCategoryDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void save_ShouldSaveAndReturnCategory() {
        // Arrange
        Category newCategory = new Category();
        newCategory.setName("Entertainment");
        newCategory.setDescription("Entertainment expenses");

        Category savedCategory = new Category();
        savedCategory.setId(3L);
        savedCategory.setName("Entertainment");
        savedCategory.setDescription("Entertainment expenses");
        savedCategory.setCreatedAt(LocalDateTime.now());

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        Category result = categoryService.save(newCategory);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Entertainment", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteById_WhenCategoryExists_ShouldDelete() {
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
    void deleteById_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.deleteById(999L);
        });

        verify(categoryRepository, times(1)).existsById(999L);
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsById_WhenCategoryExists_ShouldReturnTrue() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = categoryService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(categoryRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_WhenCategoryDoesNotExist_ShouldReturnFalse() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = categoryService.existsById(999L);

        // Assert
        assertFalse(result);
        verify(categoryRepository, times(1)).existsById(999L);
    }

    @Test
    void count_ShouldReturnTotalCategories() {
        // Arrange
        when(categoryRepository.count()).thenReturn(5L);

        // Act
        long result = categoryService.count();

        // Assert
        assertEquals(5L, result);
        verify(categoryRepository, times(1)).count();
    }
}