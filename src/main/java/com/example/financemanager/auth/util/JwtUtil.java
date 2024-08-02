package com.example.financemanager.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    private JwtUtil() {
    }

    public String extractUsername(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    public Cookie createToken(String userName) {
        long expirationTime = System.currentTimeMillis() + 900_000; // 15 minutes
        String token = JWT.create()
                .withSubject(userName)
                .withExpiresAt(new Date(expirationTime))
                .sign(Algorithm.HMAC256(secret));

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to false only for local testing, when set to true https is required
        cookie.setPath("/");
        cookie.setMaxAge((int) (expirationTime / 1000));
        return cookie;
    }

    public boolean validateToken(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
            jwtVerifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            LOGGER.error("Failed to validate token", e);
            return false;
        }
    }
}