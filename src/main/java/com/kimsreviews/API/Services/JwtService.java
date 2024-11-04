package com.kimsreviews.API.Services;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Function;

public interface JwtService {
    String extractToken(HttpServletRequest request);
    boolean validateToken(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    Claims extractAllClaims(String token);
    Authentication getAuthentication(String token);
    UserDetails extractUserDetails(String token);
}
