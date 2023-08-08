package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.entity.Token;
import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.model.Role;
import dev.fernandohenning.springcoreproject.repository.TokenRepository;
import dev.fernandohenning.springcoreproject.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenServicesTests {
    @Mock
    private TokenRepository tokenRepository;
    @InjectMocks
    private TokenServiceImpl tokenService;
    private Token token;
    private User user;
    @BeforeEach
    void setUp(){
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password1234")
                .createdAt(LocalDateTime.parse("2015-08-04T10:11:30"))
                .role(Role.USER)
                .isLocked(false)
                .isEnabled(true)
                .build();

        token = Token.builder()
                .token("e1234e1234e1234")
                .isExpired(false)
                .isRevoked(false)
                .user(user)
                .build();
    }

    @Test
    void givenTokenObject_whenRevokeToken_thenSaveUpdatedToken(){
        given(tokenRepository.findByToken("e1234e1234e1234"))
                .willReturn(Optional.ofNullable(token));
        given(tokenRepository.save(token)).willReturn(token);

        tokenService.revokeToken(token.getToken());
        Token token = tokenRepository.findByToken("e1234e1234e1234").orElse(null);
        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.isExpired());
        Assertions.assertTrue(token.isRevoked());
    }

    @Test
    void givenTokenString_whenIsValidToken_thenTrue(){
        given(tokenRepository.findByToken("e1234e1234e1234"))
                .willReturn(Optional.ofNullable(token));

        boolean isTokenValid = tokenService.isTokenValid("e1234e1234e1234");

        Assertions.assertTrue(isTokenValid);
    }

    @Test
    void givenUserAndStringToken_whenSaveUserToken_thenReturnToken(){
        given(tokenRepository.save(token)).willReturn(token);

        tokenService.saveUserToken(user, "e1234e1234e1234");

        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void givenUser_thenRevokeAllUserTokens_thenFindAllTokensReturnNull(){
        given(tokenRepository.findAllValidTokenByUser(user.getId())).willReturn(List.of(token));
        given(tokenRepository.findByToken("e1234e1234e1234"))
                .willReturn(Optional.ofNullable(token));

        tokenService.revokeAllUserTokens(user);
        Token token = tokenRepository.findByToken("e1234e1234e1234").orElse(null);

        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.isExpired());
        Assertions.assertTrue(token.isRevoked());
    }
}
