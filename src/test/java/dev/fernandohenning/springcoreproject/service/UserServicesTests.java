package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.model.Role;
import dev.fernandohenning.springcoreproject.model.UserDetailsImpl;
import dev.fernandohenning.springcoreproject.repository.UserRepository;
import dev.fernandohenning.springcoreproject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServicesTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    @BeforeEach
    void setUp(){
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password1234")
                .passwordConfirmation("password1234")
                .createdAt(LocalDateTime.parse("2015-08-04T10:11:30"))
                .role(Role.USER)
                .isLocked(false)
                .isEnabled(false)
                .build();
    }

    @Test
    void givenUserEmail_whenLoadUserByUsername_thenReturnUserDetailsImpl(){
        given(userRepository.findByEmail("john.doe@example.com")).willReturn(Optional.ofNullable(user));

        UserDetailsImpl userDetails = (UserDetailsImpl) userService.loadUserByUsername(user.getEmail());

        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(1L, userDetails.user().getId());
        Assertions.assertEquals("john.doe@example.com", userDetails.user().getEmail());
    }

    @Test
    void givenUserEmail_whenEmailExists_thenReturnTrue(){
        given(userRepository.existsByEmail("john.doe@example.com")).willReturn(true);

        boolean existsByEmail = userService.emailExists(user.getEmail());
        Assertions.assertTrue(existsByEmail);
    }
    @Test
    void givenUserEmail_whenFindUserByEmail_thenReturnUser(){
        given(userRepository.findByEmail("john.doe@example.com")).willReturn(Optional.ofNullable(user));

        User userFound = userService.findUserByEmail(user.getEmail());

        Assertions.assertNotNull(userFound);
        Assertions.assertEquals(1L, userFound.getId());
        Assertions.assertEquals("john.doe@example.com", userFound.getEmail());
    }

    @Test
    void givenUser_whenSaveUser_thenReturnUser(){
        given(userRepository.save(user)).willReturn(user);
        given(passwordEncoder.encode("password1234")).willReturn("encoded");

        User userSaved = userService.saveUser(user);

        Assertions.assertNotNull(userSaved);
        Assertions.assertEquals(1L, userSaved.getId());
        Assertions.assertEquals("john.doe@example.com", userSaved.getEmail());
        Assertions.assertEquals("encoded", userSaved.getPassword());
    }

    @Test
    void givenUser_whenUpdatePassword_thenReturnUserWithNewPassword(){
        given(userRepository.findByEmail("john.doe@example.com")).willReturn(Optional.ofNullable(user));
        given(userRepository.existsByEmail("john.doe@example.com")).willReturn(true);
        given(passwordEncoder.encode("newPassword")).willReturn("encoded");

        given(userRepository.save(user)).willReturn(user);

        userService.updatePassword("john.doe@example.com", "newPassword", "newPassword");

        User userUpdated = userService.findUserByEmail("john.doe@example.com");
        Assertions.assertNotNull(userUpdated);
        Assertions.assertEquals("encoded", userUpdated.getPassword());
    }
    @Test
    void givenEmailAndPassword_whenValidateCredentials_thenReturnUser(){
        given(userRepository.findByEmail("john.doe@example.com")).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches("password1234", user.getPassword())).willReturn(true);

        User userValidated = userService.validateCredentials("john.doe@example.com", "password1234");

        Assertions.assertNotNull(userValidated);
        Assertions.assertEquals("john.doe@example.com", userValidated.getEmail());
    }
    @Test
    void givenEmail_whenEnableUser_thenUpdateUser(){
        given(userRepository.findByEmail("john.doe@example.com")).willReturn(Optional.ofNullable(user));
        given(userRepository.save(user)).willReturn(user);

        userService.enableUser("john.doe@example.com");

        User userEnabled = userService.findUserByEmail("john.doe@example.com");

        Assertions.assertNotNull(userEnabled);
        Assertions.assertTrue(userEnabled.isEnabled());
    }
}
