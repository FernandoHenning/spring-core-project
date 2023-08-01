package dev.fernandohenning.springcoreproject.repository;

import dev.fernandohenning.springcoreproject.entity.Token;
import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class TokenRepositoryTests {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenFindAllValidTokenByUser_ThenReturnListOfTokens(){
        User user = userRepository.save(User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password1234")
                .passwordConfirmation("password1234")
                .role(Role.MODERATOR)
                .isEnabled(false)
                .isLocked(false)
                .build());

        Token token = Token.builder()
                .token("e1234f1234g1234")
                .isRevoked(false)
                .isExpired(false)
                .user(user)
                .build();

        tokenRepository.save(token);

        List<Token> tokens = tokenRepository.findAllValidTokenByUser(user.getId());

        Assertions.assertNotNull(tokens);
        Assertions.assertFalse(tokens.isEmpty());

        Assertions.assertEquals(token.getToken(), tokens.get(0).getToken());
        Assertions.assertEquals(token.isRevoked(), tokens.get(0).isRevoked());
        Assertions.assertEquals(token.isExpired(), tokens.get(0).isExpired());
        Assertions.assertEquals(token.getUser().getId(), tokens.get(0).getUser().getId());
    }

    @Test
    void whenFindByToken_ThenReturnToken(){

        Token token = Token.builder()
                .token("e1234f1234g1234")
                .isRevoked(false)
                .isExpired(false)
                .build();

        tokenRepository.save(token);

        Optional<Token> tokenResponse = tokenRepository.findByToken(token.getToken());

        Assertions.assertFalse(tokenResponse.isEmpty());
        Assertions.assertEquals("e1234f1234g1234", tokenResponse.get().getToken());
    }
}
