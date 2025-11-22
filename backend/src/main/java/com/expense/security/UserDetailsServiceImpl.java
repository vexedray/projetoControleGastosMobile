package com.expense.security;

import com.expense.model.User;
import com.expense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== LOADING USER BY EMAIL ===");
        System.out.println("Email: " + email);
        
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            System.out.println("User NOT FOUND in database");
            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }
        
        System.out.println("User FOUND:");
        System.out.println("- ID: " + user.getId());
        System.out.println("- Name: " + user.getName());
        System.out.println("- Email: " + user.getEmail());
        System.out.println("- Password hash: " + user.getPassword());
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            new ArrayList<>()
        );
    }
}