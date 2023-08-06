package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.config.JwtConfigProperties;
import dev.fernandohenning.springcoreproject.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTests {
    @Mock
    private JwtConfigProperties jwtConfigProperties;
    @InjectMocks
    private JwtServiceImpl jwtService;


    @BeforeEach
    void setUp(){
        final String mockSecretKey = "26983005172254116219179792015491640141170759388745380521399078627121442296266799";
        when(jwtConfigProperties.getSecretKey()).thenReturn(mockSecretKey);
    }

    @Test
    void testExtractClaim() {
        String token = createMockToken();
        String expectedClaimValue = "sampleClaimValue";
        Function<Claims, String> mockClaimsResolver = claims -> expectedClaimValue;
        String actualClaimValue = jwtService.extractClaim(token, mockClaimsResolver);

        Assertions.assertEquals(expectedClaimValue, actualClaimValue);
    }
    @Test
    void testExtractUsername() {
        String username = "john.doe@example.com";
        String token = createMockTokenWithSubject(username);

        String extractedUsername = jwtService.extractUsername(token);
        Assertions.assertEquals(username, extractedUsername);
    }
    @Test
    void testExtractExpiration() {
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        String token = createMockTokenWithExpiration(expirationDate);

        Date extractedExpirationDate = jwtService.extractExpiration(token);

        Assertions.assertEquals(expirationDate.toString(), extractedExpirationDate.toString());
    }


    private String createMockToken() {
        return Jwts.builder()
                .setSubject("sampleSubject")
                .signWith(jwtService.getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private String createMockTokenWithSubject(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .signWith(jwtService.getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private String createMockTokenWithExpiration(Date expirationDate) {
        return Jwts.builder()
                .setSubject("sampleSubject")
                .setExpiration(expirationDate)
                .signWith(jwtService.getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
