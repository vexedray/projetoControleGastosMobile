package com.expense.dto.response;

/**
 * DTO for authentication response with JWT token
 */
public class LoginResponseDTO {
    
    private String token;
    private String type = "Bearer";
    private String email;
    private String name;
    
    // Constructors
    public LoginResponseDTO() {
    }
    
    public LoginResponseDTO(String token, String email, String name) {
        this.token = token;
        this.email = email;
        this.name = name;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
