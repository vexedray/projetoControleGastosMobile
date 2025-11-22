package com.expense.mapper;

import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    /**
     * Converts UserRequestDTO to User entity
     */
    public User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }
    
    /**
     * Converts User entity to UserResponseDTO
     */
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
    
    /**
     * Updates an existing User entity with DTO data
     */
    public void updateEntity(User user, UserRequestDTO dto) {
        if (user == null || dto == null) {
            return;
        }
        
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
    }
}