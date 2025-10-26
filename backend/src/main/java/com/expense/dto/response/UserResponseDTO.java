package com.expense.dto.response;

/**
 * DTO for user response data
 */
public class UserResponseDTO {
    
    private Long id;
    private String name;
    private String email;

    // Constructors
    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
