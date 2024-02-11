package com.example.financemanager.auth.controller;

import com.example.financemanager.auth.model.Customer;
import com.example.financemanager.auth.model.Login;
import com.example.financemanager.auth.service.AuthService;
import com.example.financemanager.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private AuthService authService;
    private JwtUtil jwtUtil;
    private AuthRepository authRepository;

    public AuthController(AuthService authService, JwtUtil jwtUtil, AuthRepository authRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.authRepository = authRepository;

    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        try {
            authService.registerUser(customer);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e){
            LOGGER.error("Unexpected error while trying to register new user:", e);
            return ResponseEntity.badRequest().body("User registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody Login login) {
        boolean isAuthenticated = authService.validateUser(login.getUsernameOrEmail(), login.getPassword());
        if (isAuthenticated) {
            return ResponseEntity.ok(jwtUtil.createToken(login.getUsernameOrEmail()));
        } else {
            return ResponseEntity.badRequest().body("Failed to login user");
        }
    }
}