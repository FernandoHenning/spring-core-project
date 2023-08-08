package dev.fernandohenning.springcoreproject.service.impl;

import dev.fernandohenning.springcoreproject.config.JwtConfigProperties;
import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final Date issuedAt = new Date(System.currentTimeMillis());

    private JwtConfigProperties jwtConfigProperties;

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Map<String, Object> getUserClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("enabled", user.isEnabled());
        return claims;
    }

    @Override
    public String generateToken(String username, long expirationTimeInMs) {
        final Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeInMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(String username) {
        return generateToken(username, jwtConfigProperties.getRefreshTokenExpiration());
    }

    @Override
    public String generateTokenForEnableAccount(String username) {
        return generateToken(username, jwtConfigProperties.getEnableAccountExpiration());
    }

    @Override
    public String generateTokenForResetPassword(String username) {
        return generateToken(username, jwtConfigProperties.getResetPasswordExpiration());
    }

    @Override
    public String generateAccessToken(User user) {
        final Date accessTokenExpirationDate = new Date(System.currentTimeMillis() + jwtConfigProperties.getAccessTokenExpiration());
        return Jwts
                .builder()
                .setClaims(getUserClaims(user))
                .setSubject(user.getEmail())
                .setIssuedAt(issuedAt)
                .setExpiration(accessTokenExpirationDate)
                .signWith(getSigninKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Key getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfigProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
