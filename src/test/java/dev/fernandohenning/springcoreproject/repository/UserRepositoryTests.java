package dev.fernandohenning.springcoreproject.repository;

import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private static final LocalDateTime currentDateTime = LocalDateTime.now();


    @Test
    void whenFindByEmail_thenReturnUser(){
        User created = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password1234")
                .passwordConfirmation("password1234")
                .createdAt(currentDateTime)
                .role(Role.MODERATOR)
                .isEnabled(false)
                .isLocked(false)
                .build();

        userRepository.save(created);

        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("John", found.get().getFirstName());
        Assertions.assertEquals("Doe", found.get().getLastName());
        Assertions.assertEquals("john.doe@example.com", found.get().getEmail());
        Assertions.assertEquals("password1234", found.get().getPassword());
        Assertions.assertTrue(currentDateTime.isBefore(found.get().getCreatedAt().plusSeconds(1)));
        Assertions.assertEquals(Role.MODERATOR, found.get().getRole());
        Assertions.assertFalse(found.get().isEnabled());
        Assertions.assertFalse(found.get().isLocked());
    }

    @Test
    void whenExistsByEmail_thenReturnBoolean(){
        User created = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password1234")
                .passwordConfirmation("password1234")
                .createdAt(currentDateTime)
                .role(Role.MODERATOR)
                .isEnabled(false)
                .isLocked(false)
                .build();

        userRepository.save(created);

       boolean userExists = userRepository.existsByEmail("john.doe@example.com");
        Assertions.assertTrue(userExists);
    }
}
