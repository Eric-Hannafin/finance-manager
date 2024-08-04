package com.example.financemanager.auth.controller;

import com.example.financemanager.auth.model.Customer;
import com.example.financemanager.auth.model.Login;
import com.example.financemanager.auth.service.AuthService;
import com.example.financemanager.auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer) {
        try {
            authService.registerUser(customer);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            LOGGER.error("Unexpected error while trying to register new user:", e);
            return ResponseEntity.badRequest().body("User registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody Login login, HttpServletResponse response) {
        final long accessTokenExpirationTime = System.currentTimeMillis() + 900_000; // 15 minutes
        final long refreshTokenExpirationTime = System.currentTimeMillis() + 604800; // 7 days
        boolean isAuthenticated = authService.validateUser(login.getUsernameOrEmail(), login.getPassword());
        if (isAuthenticated) {
            Cookie accessToken = jwtUtil.createToken(login.getUsernameOrEmail(), accessTokenExpirationTime);
            Cookie refreshToken = jwtUtil.createToken(login.getUsernameOrEmail(), refreshTokenExpirationTime);
            response.addCookie(accessToken);
            response.addCookie(refreshToken);
            return ResponseEntity.ok().body("User authenticated successfully");
        } else {
            return ResponseEntity.status(401).body("Failed to login user");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshUserSession(@RequestBody Login login, HttpServletResponse response) {
        final long accessTokenExpirationTime = System.currentTimeMillis() + 900_000; // 15 minutes
        return null;
    }

}