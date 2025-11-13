package com.expense.controller;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.mapper.CategoryMapper;
import com.expense.model.Category;
import com.expense.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryMapper categoryMapper;

    private Category category;
    private CategoryRequestDTO requestDTO;
    private CategoryResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setDescription("Food and beverages");

        requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Food");
        requestDTO.setDescription("Food and beverages");

        responseDTO = new CategoryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Food");
        responseDTO.setDescription("Food and beverages");
    }

    @Test
    void getAllCategories_ShouldReturnCategoryList() throws Exception {
        // Arrange
        List<Category> categories = Arrays.asList(category);

        when(categoryService.findAll()).thenReturn(categories);
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[0].description").value("Food and beverages"));

        verify(categoryService, times(1)).findAll();
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDTO(category)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.description").value("Food and beverages"));

        verify(categoryService, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(999L);
    }

    @Test
    void createCategory_WithValidData_ShouldReturnCreatedCategory() throws Exception {
        // Arrange
        when(categoryMapper.toEntity(any(CategoryRequestDTO.class))).thenReturn(category);
        when(categoryService.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.description").value("Food and beverages"));

        verify(categoryService, times(1)).save(any(Category.class));
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

        verify(categoryService, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenCategoryExists_ShouldReturnUpdatedCategory() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(categoryService.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"));

        verify(categoryService, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(categoryService, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_WhenCategoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, never()).deleteById(anyLong());
    }

    @Test
    void testEndpoint_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        when(categoryService.findAll()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("API working! Number of categories: 1"));

        verify(categoryService, times(1)).findAll();
    }
}
