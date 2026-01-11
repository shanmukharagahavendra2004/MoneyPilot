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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repo.findByUsername(username)   // ✅ FIXED
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with username: " + username)
                );

        return new UserPrincipal(user);
    }
}

