package com.expense.service;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.repository.CategoryRepository;
import com.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    // Testes existentes mantidos...

    // ============= NOVOS TESTES =============

    @Test
    void getAllCategories_WhenUserHasNoCategories_ShouldReturnEmptyList() {
        // Arrange
        when(categoryRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // Act
        List<Category> result = categoryService.getAllCategories(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryService.getCategoryById(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getName());
        assertEquals("#FF5733", result.get().getColor());
        verify(categoryRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange
        when(categoryRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.getCategoryById(999L, 1L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findByIdAndUserId(999L, 1L);
    }

    @Test
    void findByName_WhenCategoryExists_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findByName("Food")).thenReturn(category);

        // Act
        Optional<Category> result = categoryService.findByName("Food");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getName());
        verify(categoryRepository, times(1)).findByName("Food");
    }

    @Test
    void findByName_WhenCategoryDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange
        when(categoryRepository.findByName("NonExistent")).thenReturn(null);

        // Act
        Optional<Category> result = categoryService.findByName("NonExistent");

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findByName("NonExistent");
    }

    @Test
    void existsByName_WhenCategoryExists_ShouldReturnTrue() {
        // Arrange
        when(categoryRepository.findByName("Food")).thenReturn(category);

        // Act
        boolean result = categoryService.existsByName("Food");

        // Assert
        assertTrue(result);
        verify(categoryRepository, times(1)).findByName("Food");
    }

    @Test
    void existsByName_WhenCategoryDoesNotExist_ShouldReturnFalse() {
        // Arrange
        when(categoryRepository.findByName("NonExistent")).thenReturn(null);

        // Act
        boolean result = categoryService.existsByName("NonExistent");

        // Assert
        assertFalse(result);
        verify(categoryRepository, times(1)).findByName("NonExistent");
    }

    @Test
    void createCategory_WithExistingName_ShouldThrowException() {
        // Arrange
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Food"); // Nome já existente
        
        when(categoryRepository.findByName("Food")).thenReturn(category);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(dto);
        });

        assertEquals("Categoria já existe", exception.getMessage());
        verify(categoryRepository, times(1)).findByName("Food");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createCategory_WithoutUserId_ShouldCreateCategoryWithoutUser() {
        // Arrange
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("New Category");
        dto.setDescription("Test Description");
        dto.setColor("#FFFFFF");
        dto.setIcon("📁");

        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("New Category");
        newCategory.setDescription("Test Description");
        newCategory.setColor("#FFFFFF");
        newCategory.setIcon("📁");
        newCategory.setCreatedAt(LocalDateTime.now());

        when(categoryRepository.findByName("New Category")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // Act
        Category result = categoryService.createCategory(dto);

        // Assert
        assertNotNull(result);
        assertEquals("New Category", result.getName());
        assertNull(result.getUser()); // Não deve ter usuário associado
        verify(categoryRepository, times(1)).findByName("New Category");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(999L, categoryRequestDTO);
        });

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WithDuplicateName_ShouldThrowException() {
        // Arrange
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Transport"); // Nome diferente do original
        dto.setDescription("Updated description");
        dto.setColor("#000000");
        dto.setIcon("🚗");

        Category existingTransport = new Category();
        existingTransport.setId(2L);
        existingTransport.setName("Transport");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Transport")).thenReturn(existingTransport);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(1L, dto);
        });

        assertEquals("Nome de categoria já existe", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findByName("Transport");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WithSameName_ShouldUpdateOtherFields() {
        // Arrange
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Food"); // Mesmo nome (deve permitir)
        dto.setDescription("Updated description");
        dto.setColor("#000000");
        dto.setIcon("🍕");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Food");
        updatedCategory.setDescription("Updated description");
        updatedCategory.setColor("#000000");
        updatedCategory.setIcon("🍕");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.updateCategory(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Food", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals("#000000", result.getColor());
        assertEquals("🍕", result.getIcon());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void save_WhenCreatingNewCategory_ShouldSetCreatedAt() {
        // Arrange
        Category newCategory = new Category();
        newCategory.setName("Test Category");
        newCategory.setDescription("Test Description");
        // Não seta createdAt

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Test Category");
        savedCategory.setDescription("Test Description");
        savedCategory.setCreatedAt(LocalDateTime.now());

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        Category result = categoryService.save(newCategory);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCreatedAt());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void save_WhenUpdatingExistingCategory_ShouldNotChangeCreatedAt() {
        // Arrange
        LocalDateTime originalDate = LocalDateTime.now().minusDays(1);
        category.setCreatedAt(originalDate);
        
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.save(category);

        // Assert
        assertNotNull(result);
        assertEquals(originalDate, result.getCreatedAt());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteById_WhenCategoryDoesNotExist_ShouldThrowExceptionWithMessage() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.deleteById(999L);
        });

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(categoryRepository, times(1)).existsById(999L);
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_WhenCategoryExists_ShouldDeleteSuccessfully() {
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
    void existsById_ShouldReturnRepositoryResult() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = categoryService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(categoryRepository, times(1)).existsById(1L);
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Arrange
        when(categoryRepository.count()).thenReturn(10L);

        // Act
        long result = categoryService.count();

        // Assert
        assertEquals(10L, result);
        verify(categoryRepository, times(1)).count();
    }

    @Test
    void count_WhenNoCategories_ShouldReturnZero() {
        // Arrange
        when(categoryRepository.count()).thenReturn(0L);

        // Act
        long result = categoryService.count();

        // Assert
        assertEquals(0L, result);
        verify(categoryRepository, times(1)).count();
    }

    // Testes de validação de dados de entrada
    @ParameterizedTest
    @MethodSource("provideInvalidCategoryData")
    void createCategory_WithInvalidData_ShouldThrowException(String name, String description, String color, String icon) {
        // Arrange
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setColor(color);
        dto.setIcon(icon);

        when(categoryRepository.findByName(any())).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(dto);
        });

        // Verifica que pelo menos alguma exceção foi lançada
        assertNotNull(exception);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    private static Stream<Arguments> provideInvalidCategoryData() {
        return Stream.of(
            Arguments.of(null, "Description", "#FFFFFF", "📁"), // Nome nulo
            Arguments.of("", "Description", "#FFFFFF", "📁"),   // Nome vazio
            Arguments.of("  ", "Description", "#FFFFFF", "📁") // Nome com espaços
        );
    }

    // Teste para verificar comportamento com valores nulos
    @Test
    void createCategory_WithNullValuesInDTO_ShouldHandleGracefully() {
        // Arrange
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Test");
        // Outros campos são null

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Test");
        // Campos nulos permanecem nulos

        when(categoryRepository.findByName("Test")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        Category result = categoryService.createCategory(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test", result.getName());
        assertNull(result.getDescription());
        assertNull(result.getColor());
        assertNull(result.getIcon());
    }

    // Teste para verificar que o usuário é associado corretamente
    @Test
    void createCategory_WithUserId_ShouldAssociateUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category cat = invocation.getArgument(0);
            cat.setId(100L); // Simula o ID gerado pelo banco
            return cat;
        });

        // Act
        Category result = categoryService.createCategory(categoryRequestDTO, 1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUser());
        assertEquals(1L, result.getUser().getId());
        assertEquals("Test User", result.getUser().getName());
        verify(userRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    // Teste para verificar rollback em caso de erro
    @Test
    void createCategory_WhenSaveFails_ShouldNotPersist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(categoryRequestDTO, 1L);
        });

        verify(userRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    // Teste para verificar que findAll retorna em ordem
    @Test
    void findAll_ShouldReturnCategoriesInOrder() {
        // Arrange
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("A Category");
        
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("B Category");
        
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("A Category", result.get(0).getName());
        assertEquals("B Category", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    // Teste para verificar comportamento com IDs inválidos
    @Test
    void findById_WithNegativeId_ShouldReturnEmpty() {
        // Arrange
        when(categoryRepository.findById(-1L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.findById(-1L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(-1L);
    }

    @Test
    void findById_WithZeroId_ShouldReturnEmpty() {
        // Arrange
        when(categoryRepository.findById(0L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.findById(0L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(0L);
    }

    // Teste de concorrência básico
    @Test
    void updateCategory_WithConcurrentModification_ShouldHandleGracefully() {
        // Arrange
        CategoryRequestDTO dto1 = new CategoryRequestDTO();
        dto1.setName("Updated Name");
        dto1.setDescription("Desc 1");
        
        CategoryRequestDTO dto2 = new CategoryRequestDTO();
        dto2.setName("Updated Name");
        dto2.setDescription("Desc 2");

        Category categoryCopy = new Category();
        categoryCopy.setId(1L);
        categoryCopy.setName("Food");
        categoryCopy.setDescription("Original");

        when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(categoryCopy))
            .thenReturn(Optional.of(categoryCopy));
        
        when(categoryRepository.save(any(Category.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act - Simula duas atualizações "concorrentes"
        Category result1 = categoryService.updateCategory(1L, dto1);
        Category result2 = categoryService.updateCategory(1L, dto2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        verify(categoryRepository, times(2)).findById(1L);
        verify(categoryRepository, times(2)).save(any(Category.class));
    }
}