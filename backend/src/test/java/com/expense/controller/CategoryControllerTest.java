package com.expense.controller;

import com.expense.dto.request.CategoryRequestDTO;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.mapper.CategoryMapper;
import com.expense.model.Category;
import com.expense.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@DisplayName("Testes do CategoryController")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private Category category1;
    private Category category2;
    private CategoryRequestDTO categoryRequestDTO;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setId(1L);
        category1.setName("Alimentação");
        category1.setDescription("Despesas com alimentação");
        category1.setCreatedAt(LocalDateTime.now());

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Transporte");
        category2.setDescription("Despesas com transporte");
        category2.setCreatedAt(LocalDateTime.now());

        categoryRequestDTO = new CategoryRequestDTO(
            "Alimentação",
            "Despesas com alimentação"
        );
    }

    @Test
    @DisplayName("Deve retornar todas as categorias")
    void getAllCategories_ShouldReturnAllCategories() throws Exception {
        // Arrange
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryService.findAll()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alimentação")))
                .andExpect(jsonPath("$[0].description", is("Despesas com alimentação")))
                .andExpect(jsonPath("$[1].name", is("Transporte")))
                .andExpect(jsonPath("$[1].description", is("Despesas com transporte")));

        verify(categoryService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar categoria por ID quando existir")
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category1));

        // Act & Assert
        mockMvc.perform(get("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alimentação")))
                .andExpect(jsonPath("$.description", is("Despesas com alimentação")));

        verify(categoryService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando categoria não existir por ID")
    void getCategoryById_WhenCategoryNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve criar nova categoria com sucesso")
    void createCategory_WithValidData_ShouldCreateCategory() throws Exception {
        // Arrange
        when(categoryService.save(any(Category.class))).thenReturn(category1);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Alimentação")))
                .andExpect(jsonPath("$.description", is("Despesas com alimentação")));

        verify(categoryService, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar categoria com dados inválidos")
    void createCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CategoryRequestDTO invalidDTO = new CategoryRequestDTO("", "");

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve atualizar categoria existente")
    void updateCategory_WhenCategoryExists_ShouldUpdateCategory() throws Exception {
        // Arrange
        CategoryRequestDTO updateDTO = new CategoryRequestDTO(
            "Alimentação Atualizada",
            "Descrição atualizada"
        );

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Alimentação Atualizada");
        updatedCategory.setDescription("Descrição atualizada");
        updatedCategory.setCreatedAt(category1.getCreatedAt());

        when(categoryService.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryService.save(any(Category.class))).thenReturn(updatedCategory);

        // Act & Assert
        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alimentação Atualizada")))
                .andExpect(jsonPath("$.description", is("Descrição atualizada")));

        verify(categoryService, times(1)).findById(1L);
        verify(categoryService, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar categoria inexistente")
    void updateCategory_WhenCategoryNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(999L);
        verify(categoryService, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve deletar categoria existente")
    void deleteCategory_WhenCategoryExists_ShouldDeleteCategory() throws Exception {
        // Arrange
        when(categoryService.findById(1L)).thenReturn(Optional.of(category1));
        doNothing().when(categoryService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).findById(1L);
        verify(categoryService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar categoria inexistente")
    void deleteCategory_WhenCategoryNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(999L);
        verify(categoryService, never()).deleteById(anyLong());
    }
}
