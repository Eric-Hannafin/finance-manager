package com.example.financemanager.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.financemanager.auth.model.Login;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private JwtUtil(){}

    public String generateToken(Login login){
        return JWT.create()
                .withSubject(login.getUsernameOrEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .sign(Algorithm.HMAC256(secret));
    }
}