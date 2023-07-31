package dev.fernandohenning.springcoreproject.requests.validations.user;

import dev.fernandohenning.springcoreproject.dto.user.request.CreateUserRequest;
import dev.fernandohenning.springcoreproject.model.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

@ExtendWith(SpringExtension.class)
class UserRequestsValidationsTests {
    private Validator validator;

    @BeforeEach
    void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validCreateUserRequest(){
        CreateUserRequest request = new CreateUserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "password123",
                Role.USER
        );

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    void invalidCreateUserRequest_InvalidFirstName() {
        CreateUserRequest request = new CreateUserRequest(
                "J",
                "Doe",
                "john.doe@example.com",
                "password123",
                "password123",
                Role.USER
        );
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    void invalidCreateUserRequest_InvalidEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "John",
                "Doe",
                "invalid_email",
                "password123",
                "password123",
                Role.USER
        );
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    void invalidCreateUserRequest_InvalidPassword() {
        CreateUserRequest request = new CreateUserRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "1234",
                "1234",
                Role.USER
        );
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }
}
