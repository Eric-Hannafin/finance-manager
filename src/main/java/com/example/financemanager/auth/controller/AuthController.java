package com.example.financemanager.auth.controller;

import com.example.financemanager.auth.model.Customer;
import com.example.financemanager.auth.model.LoginRequest;
import com.example.financemanager.auth.service.AuthService;
import com.example.financemanager.auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";


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
    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequest login, HttpServletResponse response) {
        final long accessTokenExpirationTime = System.currentTimeMillis() + 900_000;
        final long refreshTokenExpirationTime = System.currentTimeMillis() + 604800;
        boolean isAuthenticated = authService.validateUser(login.getUsernameOrEmail(), login.getPassword());
        if (isAuthenticated) {
            Cookie accessToken = jwtUtil.createToken(accessTokenExpirationTime, ACCESS_TOKEN);
            Cookie refreshToken = jwtUtil.createToken(refreshTokenExpirationTime, REFRESH_TOKEN);
            response.addCookie(accessToken);
            response.addCookie(refreshToken);
            return ResponseEntity.ok().body("User authenticated successfully");
        } else {
            return ResponseEntity.status(401).body("Failed to login user");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshUserSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN.equals(cookie.getName())) {
                if (jwtUtil.validateToken(cookie.getValue())) {
                    long accessTokenExpirationTime = System.currentTimeMillis() + 900_000;
                    Cookie accessToken = jwtUtil.createToken(accessTokenExpirationTime, ACCESS_TOKEN);
                    response.addCookie(accessToken);
                    return ResponseEntity.ok().body("Refresh token accepted and new access token provided");
                }
            }
        }
        return ResponseEntity.status(401).body("Refresh token accepted and new access token provided");
    }
}