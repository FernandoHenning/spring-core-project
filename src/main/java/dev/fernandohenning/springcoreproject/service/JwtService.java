package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService{
         <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
         String extractUsername(String token);
         Date extractExpiration(String token);
         Map<String, Object> getUserClaims(User user);
         String generateToken(String username, long expirationTimeInMs);
         String generateRefreshToken(String username);
         String generateTokenForEnableAccount(String username);
         String generateTokenForResetPassword(String username);
         String generateAccessToken(User user);
         boolean isTokenValid(String token, UserDetails userDetails);
         boolean isTokenExpired(String token);
         Claims extractAllClaims(String token);
         Key getSigninKey();
}
