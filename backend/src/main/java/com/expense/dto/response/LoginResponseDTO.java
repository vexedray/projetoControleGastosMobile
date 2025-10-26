package com.expense.dto.response;

/**
 * DTO for authentication response with JWT token
 */
public class LoginResponseDTO {
    
    private String token;
    private String type = "Bearer";
    private UserResponseDTO user;
    
    // Constructors
    public LoginResponseDTO() {
    }
    
    public LoginResponseDTO(String token, UserResponseDTO user) {
        this.token = token;
        this.user = user;
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
    
    public UserResponseDTO getUser() {
        return user;
    }
    
    public void setUser(UserResponseDTO user) {
        this.user = user;
    }
}
