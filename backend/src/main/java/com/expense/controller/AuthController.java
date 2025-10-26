package com.expense.controller;

import com.expense.dto.request.LoginRequestDTO;
import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.LoginResponseDTO;
import com.expense.dto.response.UserResponseDTO;
import com.expense.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("POST /api/auth/register - Registering new user: {}", userRequestDTO.getEmail());
        
        try {
            // Check if email is already in use
            if (!authService.isEmailAvailable(userRequestDTO.getEmail())) {
                logger.warn("Email {} is already in use", userRequestDTO.getEmail());
                return ResponseEntity.badRequest().body("Email is already in use");
            }
            
            UserResponseDTO user = authService.register(userRequestDTO);
            logger.info("User registered successfully: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
            
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.info("POST /api/auth/login - Login attempt for: {}", loginRequest.getEmail());
        
        try {
            LoginResponseDTO response = authService.login(loginRequest);
            logger.info("Login successful for: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Login failed for {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
    
    /**
     * Check if email is available
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        logger.info("GET /api/auth/check-email - Checking availability of: {}", email);
        boolean available = authService.isEmailAvailable(email);
        return ResponseEntity.ok(new EmailAvailabilityResponse(available));
    }
    
    // Inner class for email availability response
    static class EmailAvailabilityResponse {
        private boolean available;
        
        public EmailAvailabilityResponse(boolean available) {
            this.available = available;
        }
        
        public boolean isAvailable() {
            return available;
        }
        
        public void setAvailable(boolean available) {
            this.available = available;
        }
    }
}
