package com.example.financemanager.auth.controller;

import com.example.financemanager.auth.model.Customer;
import com.example.financemanager.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer userDetails){
        try {
            authService.registerUser(userDetails);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e){
            // Log here
            return ResponseEntity.badRequest().body("User registration failed");
        }
    }
}