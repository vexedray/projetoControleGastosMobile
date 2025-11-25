package com.expense.assembler;

import com.expense.controller.CategoryController;
import com.expense.controller.ExpenseController;
import com.expense.dto.response.CategoryResponseDTO;
import com.expense.model.hateoas.CategoryModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CategoryModelAssembler extends RepresentationModelAssemblerSupport<CategoryResponseDTO, CategoryModel> {
    
    public CategoryModelAssembler() {
        super(CategoryController.class, CategoryModel.class);
    }
    
    @Override
    public CategoryModel toModel(CategoryResponseDTO dto) {
        CategoryModel model = new CategoryModel(
            dto.getId(), 
            dto.getName(), 
            dto.getDescription(), 
            dto.getColor(), 
            dto.getIcon()
        );
        
        // Link para o próprio recurso (self)
        model.add(linkTo(methodOn(CategoryController.class).getCategoryById(dto.getId())).withSelfRel());
        
        // Link para listar todas as categorias
        model.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("categories"));
        
        // Link para atualizar categoria
        model.add(linkTo(methodOn(CategoryController.class).updateCategory(dto.getId(), null)).withRel("update"));
        
        // Link para deletar categoria
        model.add(linkTo(methodOn(CategoryController.class).deleteCategory(dto.getId())).withRel("delete"));
        
        // Link para despesas desta categoria
        model.add(linkTo(methodOn(ExpenseController.class).getExpensesByCategory(dto.getId())).withRel("expenses"));
        
        return model;
    }
}
