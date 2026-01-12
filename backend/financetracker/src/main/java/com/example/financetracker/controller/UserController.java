package com.example.financetracker.controller;

import com.example.financetracker.model.User;
import com.example.financetracker.model.UserLogin;
import com.example.financetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        System.out.println("hello");
        return service.signup(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLogin login) {
        System.out.println(login.getUsername()+" "+ // Fixed getter
                login.getPassword());
        String result = service.verify(
                login.getUsername(), // Fixed getter
                login.getPassword()
        );
        System.out.println("User details "+result);
        return result;
    }
}
