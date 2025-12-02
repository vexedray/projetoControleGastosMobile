package com.expense.controller;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.mapper.CategoryMapper;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.model.hateoas.CategoryModel;
import com.expense.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "test@example.com")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryMapper categoryMapper;

    @MockBean
    private com.expense.security.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.expense.security.UserDetailsServiceImpl userDetailsService;

    @MockBean
    private com.expense.repository.UserRepository userRepository;

    @MockBean
    private com.expense.assembler.CategoryModelAssembler categoryModelAssembler;

    private Category category;
    private CategoryRequestDTO requestDTO;
    private CategoryResponseDTO responseDTO;
    private CategoryModel categoryModel;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Setup mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        
        // Mock userRepository to return the mock user
        when(userRepository.findByEmail(anyString())).thenReturn(mockUser);
        
        // Setup category
        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setDescription("Food and beverages");

        // Setup request DTO
        requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Food");
        requestDTO.setDescription("Food and beverages");

        // Setup response DTO
        responseDTO = new CategoryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Food");
        responseDTO.setDescription("Food and beverages");
        responseDTO.setColor("#FF5733");
        responseDTO.setIcon("🍔");

        // Setup CategoryModel (HATEOAS)
        categoryModel = new CategoryModel(1L, "Food", "Food and beverages", "#FF5733", "🍔");
        categoryModel.add(Link.of("/api/categories/1", "self"));
        categoryModel.add(Link.of("/api/categories", "categories"));

        // Mock CategoryModelAssembler
        when(categoryModelAssembler.toModel(any(CategoryResponseDTO.class)))
            .thenReturn(categoryModel);
    }

    @Test
    void getAllCategories_ShouldReturnCategoryList() throws Exception {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        when(categoryService.getAllCategories(1L)).thenReturn(categories);
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.categoryModelList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.categoryModelList[0].name").value("Food"));

        verify(categoryService, times(1)).getAllCategories(1L);
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1L, 1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDTO(category)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"));

        verify(categoryService, times(1)).getCategoryById(1L, 1L);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(999L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getCategoryById(999L, 1L);
    }

    @Test
    void createCategory_WithValidData_ShouldReturnCreatedCategory() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CategoryRequestDTO.class), eq(1L))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"));

        verify(categoryService, times(1)).createCategory(any(CategoryRequestDTO.class), eq(1L));
    }

    @Test
    void createCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CategoryRequestDTO invalidDTO = new CategoryRequestDTO();
        invalidDTO.setName("");

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(CategoryRequestDTO.class), anyLong());
    }

    @Test
    void updateCategory_WhenCategoryExists_ShouldReturnUpdatedCategory() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(1L), any(CategoryRequestDTO.class), eq(1L))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryRequestDTO.class), eq(1L));
    }

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(999L), any(CategoryRequestDTO.class), eq(1L)))
            .thenThrow(new RuntimeException("Category not found"));

        // Act & Assert
        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).updateCategory(eq(999L), any(CategoryRequestDTO.class), eq(1L));
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L, 1L);
    }

    @Test
    void deleteCategory_WhenCategoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Category not found"))
            .when(categoryService).deleteCategory(999L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).deleteCategory(999L, 1L);
    }

    @Test
    void testEndpoint_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        when(categoryService.getAllCategories(1L)).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("API working! Number of categories for user 1: 1"));

        verify(categoryService, times(1)).getAllCategories(1L);
    }
}