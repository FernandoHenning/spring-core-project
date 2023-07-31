package dev.fernandohenning.springcoreproject.requests.validations.authentication;

import dev.fernandohenning.springcoreproject.dto.authentication.request.AuthenticationRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
class AuthenticationRequestValidationsTests {
    private Validator validator;

    @BeforeEach
    void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validAuthenticationRequest(){
        AuthenticationRequest request =  new AuthenticationRequest(
                "john.doe@example.com",
                "password1234"
        );
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        Assertions.assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("generator")
    void invalidAuthenticationRequest_InvalidEmail(AuthenticationRequest request, boolean expected) {
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(expected,violations.isEmpty());
    }
    @Test
    void invalidCreateUserRequest_InvalidPassword() {
        AuthenticationRequest request = new AuthenticationRequest(
                "john.doe@example.com",
                "1234"
        );
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }

    private static Stream<Arguments> generator(){
        return Stream.of(
                Arguments.of(new AuthenticationRequest(
                        "invalid_email",
                        "password1234"
                ), false),
                Arguments.of(new AuthenticationRequest(
                        "a@",
                        "password1234"
                ), false)
        );
    }
}
