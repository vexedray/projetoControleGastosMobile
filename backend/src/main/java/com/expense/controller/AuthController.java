package com.expense.controller;

import com.expense.dto.request.LoginRequestDTO;
import com.expense.dto.request.UserRequestDTO;
import com.expense.dto.response.LoginResponseDTO;
import com.expense.model.User;
import com.expense.security.JwtTokenProvider;
import com.expense.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        System.out.println("=== AUTH CONTROLLER LOGIN ===");
        System.out.println("Email: " + loginRequest.getEmail());
        System.out.println("Password present: " + (loginRequest.getPassword() != null));
        
        try {
            // Tenta autenticar
            System.out.println("Attempting authentication...");
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
            
            System.out.println("Authentication successful!");
            System.out.println("Principal: " + authentication.getPrincipal());
            
            // Gera o token JWT
            String jwt = tokenProvider.generateToken(loginRequest.getEmail());
            System.out.println("JWT generated: " + jwt);
            
            // Busca informações do usuário
            User user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Retorna a resposta com o token
            LoginResponseDTO response = new LoginResponseDTO(jwt, user.getEmail(), user.getName());
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            System.out.println("Exception type: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciais inválidas"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDTO userRequest) {
        try {
            User user = userService.createUser(userRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuário criado com sucesso", "userId", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}