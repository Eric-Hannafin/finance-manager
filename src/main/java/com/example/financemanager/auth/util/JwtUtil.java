package com.example.financemanager.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.financemanager.auth.model.Login;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;


public class JwtUtil {

    @Value("${jwt.secret}")
    private static String secret;

    private JwtUtil(){}

    public static String generateToken(Login login){
        return JWT.create()
                .withSubject(login.getUsernameOrEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .sign(Algorithm.HMAC256(secret));
    }
}