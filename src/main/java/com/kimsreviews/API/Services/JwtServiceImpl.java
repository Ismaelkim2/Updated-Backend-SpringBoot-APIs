package com.kimsreviews.API.Services;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = Logger.getLogger(JwtServiceImpl.class.getName());

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    @Value("${jwt.header}")
    private String header;

    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtServiceImpl(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(header);
        if (bearerToken != null && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length()).trim();
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return extractAllClaims(token) != null && !isTokenExpired(token);
        } catch (JwtException e) {
            LOGGER.log(Level.SEVERE, "Token validation failed: " + e.getMessage(), e);
            return false; // Token is invalid
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during token validation: " + e.getMessage(), e);
            return false; // General error
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder() // Use parserBuilder() instead of parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .build() // Build the parser
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            LOGGER.log(Level.WARNING, "Token has expired", e);
            throw e; // Allow further handling of expired tokens if needed
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            LOGGER.log(Level.SEVERE, "Token parsing error: " + e.getMessage(), e);
            throw new RuntimeException("Token parsing failed: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during token parsing: " + e.getMessage(), e);
            throw new RuntimeException("Token parsing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = extractUserDetails(token);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public UserDetails extractUserDetails(String token) {
        String phoneNumber = extractClaim(token, Claims::getSubject);
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new UsernameNotFoundException("Phone number not found in token");
        }
        System.out.println("Extracted phone number from token: " + phoneNumber);
        return userDetailsService.loadUserByUsername(phoneNumber);
    }

}


