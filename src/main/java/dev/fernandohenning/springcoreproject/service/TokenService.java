package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.entity.Token;
import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void revokeToken(String token){
        Token storedToken = tokenRepository.findByToken(token).orElse(null);

        if (storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }

    public boolean isTokenValid(String token){
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }

    public void saveUseToken(User user, String encodedToken){
        Token token = Token.builder()
                .user(user)
                .token(encodedToken)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

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
