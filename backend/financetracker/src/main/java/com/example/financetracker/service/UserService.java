package com.example.financetracker.service;

import com.example.financetracker.model.User;
import com.example.financetracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username and password must be provided");
        }

        try {
            // Encode password
            user.setPassword(encoder.encode(user.getPassword()));
            // Save user safely
            return repo.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while signing up user: " + e.getMessage());
        }
    }

    public String verify(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "Username and password must be provided";
        }

        try {
            // Authenticate user credentials
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            Optional<User> optionalUser = repo.findByUsernameOrEmail(username);

            if (optionalUser.isEmpty()) {
                return "User not found";
            }

            User user = optionalUser.get();
            System.out.println("Authenticated user: " + user.getId() + " " + user.getUsername());

            // Generate JWT with userId + username
            return jwtService.generateToken(user.getId(), user.getUsername());

        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid username or password: " + e.getMessage();
        }
    }
}
