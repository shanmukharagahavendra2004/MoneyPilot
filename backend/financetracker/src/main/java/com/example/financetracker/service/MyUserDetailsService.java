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

        User user = repo.findByUsernameOrEmail(input)   // 🔥 THIS IS THE FIX
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with username or email: " + input)
                );

        return new UserPrincipal(user);
    }
}
