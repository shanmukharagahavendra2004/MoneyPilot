package com.example.financetracker.service;

import com.example.financetracker.model.User;
import com.example.financetracker.model.UserPrincipal;
import com.example.financetracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        try {
            // 1️⃣ Defensive: input must exist
            if (input == null || input.trim().isEmpty()) {
                throw new UsernameNotFoundException("Username or email is empty");
            }

            // 2️⃣ Lookup user safely
            User user = repo.findByUsernameOrEmail(input.trim())
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found with username or email: " + input)
                    );

            // 3️⃣ Defensive: database integrity check
            if (user.getUsername() == null || user.getPassword() == null) {
                throw new UsernameNotFoundException("Corrupted user record");
            }

            return new UserPrincipal(user);

        } catch (UsernameNotFoundException e) {
            throw e; // required by Spring Security
        } catch (Exception e) {
            // Prevent leaking DB or stacktrace info
            System.err.println("Auth lookup failed: " + e.getMessage());
            throw new UsernameNotFoundException("Authentication service failure");
        }
    }
}
