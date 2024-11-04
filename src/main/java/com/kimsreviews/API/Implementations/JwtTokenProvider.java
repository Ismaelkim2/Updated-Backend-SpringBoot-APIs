package com.kimsreviews.API.Implementations;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = "7FNWfRR6MIjRJmPPU7mZBs9vR01oExTLJ978Oj10A7U=";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    public String generateToken(String phoneNumber) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", phoneNumber);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        // Set the phone number as the subject
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(phoneNumber)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }


    public Claims extractAllClaims(String token) {
        System.out.println("Received token: " + token);

        try {
            return Jwts
                    .parserBuilder() // Use parserBuilder() instead of parser()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (MalformedJwtException e) {
            throw new RuntimeException("Token is malformed. Please verify the token format.", e);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token has expired. Please request a new token.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error extracting claims from token", e);
        }
    }
}
