package dev.fernandohenning.springcoreproject.service.impl;

import dev.fernandohenning.springcoreproject.entity.Token;
import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.repository.TokenRepository;
import dev.fernandohenning.springcoreproject.service.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;

    @Override
    public void revokeToken(String token){
        Token storedToken = tokenRepository.findByToken(token).orElse(null);

        if (storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
    @Override
    public boolean isTokenValid(String token){
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }
    @Override
    public void saveUserToken(User user, String encodedToken){
        Token token = Token.builder()
                .user(user)
                .token(encodedToken)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }
    @Override
    public void revokeAllUserTokens(User user){
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        if(validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
