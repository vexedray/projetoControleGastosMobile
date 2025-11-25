package com.expense.assembler;

import com.expense.controller.UserController;
import com.expense.controller.ExpenseController;
import com.expense.dto.response.UserResponseDTO;
import com.expense.model.hateoas.UserModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<UserResponseDTO, UserModel> {
    
    public UserModelAssembler() {
        super(UserController.class, UserModel.class);
    }
    
    @Override
    public UserModel toModel(UserResponseDTO dto) {
        UserModel model = new UserModel(dto.getId(), dto.getName(), dto.getEmail());
        
        // Link para o próprio recurso (self)
        model.add(linkTo(methodOn(UserController.class).getUserById(dto.getId())).withSelfRel());
        
        // Link para listar todos os usuários
        model.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        
        // Link para buscar por email
        model.add(linkTo(methodOn(UserController.class).getUserByEmail(dto.getEmail())).withRel("user-by-email"));
        
        // Link para atualizar usuário
        model.add(linkTo(methodOn(UserController.class).updateUser(dto.getId(), null)).withRel("update"));
        
        // Link para deletar usuário
        model.add(linkTo(methodOn(UserController.class).deleteUser(dto.getId())).withRel("delete"));
        
        // Link para despesas do usuário
        model.add(linkTo(methodOn(ExpenseController.class).getExpensesByUser(dto.getId())).withRel("expenses"));
        
        return model;
    }
}
