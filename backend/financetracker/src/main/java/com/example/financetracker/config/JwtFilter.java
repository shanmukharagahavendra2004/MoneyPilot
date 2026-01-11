package com.example.financetracker.config;

import com.example.financetracker.model.User;
import com.example.financetracker.repo.UserRepo;
import com.example.financetracker.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);

            if (username != null && userId != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepo.findById(userId).orElse(null);
                if (user == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password(user.getPassword())
                        .authorities("USER")
                        .build();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e) {
            System.out.println("JWT ERROR → " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
