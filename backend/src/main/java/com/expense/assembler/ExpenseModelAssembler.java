package com.expense.assembler;

import com.expense.controller.ExpenseController;
import com.expense.controller.UserController;
import com.expense.controller.CategoryController;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.model.hateoas.ExpenseModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ExpenseModelAssembler extends RepresentationModelAssemblerSupport<ExpenseResponseDTO, ExpenseModel> {
    
    public ExpenseModelAssembler() {
        super(ExpenseController.class, ExpenseModel.class);
    }
    
    @Override
    public ExpenseModel toModel(ExpenseResponseDTO dto) {
        ExpenseModel model = new ExpenseModel(
            dto.getId(),
            dto.getAmount(),
            dto.getDescription(),
            dto.getDate(),
            dto.getUser() != null ? dto.getUser().getId() : null,
            dto.getUser() != null ? dto.getUser().getName() : null,
            dto.getCategory() != null ? dto.getCategory().getId() : null,
            dto.getCategory() != null ? dto.getCategory().getName() : null
        );
        
        // Link para o próprio recurso (self)
        model.add(linkTo(methodOn(ExpenseController.class).getExpenseById(dto.getId())).withSelfRel());
        
        // Link para listar todas as despesas
        model.add(linkTo(methodOn(ExpenseController.class).getAllExpenses()).withRel("expenses"));
        
        // Link para o usuário da despesa
        if (dto.getUser() != null) {
            model.add(linkTo(methodOn(UserController.class).getUserById(dto.getUser().getId())).withRel("user"));
            // Link para despesas do mesmo usuário
            model.add(linkTo(methodOn(ExpenseController.class).getExpensesByUser(dto.getUser().getId())).withRel("user-expenses"));
        }
        
        // Link para a categoria da despesa
        if (dto.getCategory() != null) {
            model.add(linkTo(methodOn(CategoryController.class).getCategoryById(dto.getCategory().getId())).withRel("category"));
            // Link para despesas da mesma categoria
            model.add(linkTo(methodOn(ExpenseController.class).getExpensesByCategory(dto.getCategory().getId())).withRel("category-expenses"));
        }
        
        // Link para atualizar despesa
        model.add(linkTo(methodOn(ExpenseController.class).updateExpense(dto.getId(), null)).withRel("update"));
        
        // Link para deletar despesa
        model.add(linkTo(methodOn(ExpenseController.class).deleteExpense(dto.getId())).withRel("delete"));
        
        return model;
    }
}
