package com.expense.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login requests
 */
public class LoginRequestDTO {
    
    @NotBlank(message = "Email é obrigatório")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    private String password;
    
    // Constructors
    public LoginRequestDTO() {
    }
    
    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
