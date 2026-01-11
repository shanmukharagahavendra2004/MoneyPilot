package com.example.financetracker.service;

import com.example.financetracker.model.User;
import com.example.financetracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public User signup(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String verify(String username, String password) {
        try {
            // Authenticate user credentials
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            User user = repo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("User "+user);

            // Generate JWT with userId + username
            return jwtService.generateToken(user.getId(), user.getUsername());

        } catch (Exception e) {
            return "Invalid username or password";
        }
    }
}
