package dev.fernandohenning.springcoreproject.requests.validations.user;

import dev.fernandohenning.springcoreproject.dto.user.request.ResetUserPasswordRequest;
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
class ResetUserPasswordRequestValidationsTests {
    private Validator validator;

    @BeforeEach
    void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validResetEmailPasswordRequest(){
        ResetUserPasswordRequest request = new ResetUserPasswordRequest(
                "test@tes.com"
        );
        Set<ConstraintViolation<ResetUserPasswordRequest>> violations = validator.validate(request);
        Assertions.assertTrue(violations.isEmpty());
    }
    @Test
    void invalidResetUserPasswordRequest_InvalidEmail(){
        ResetUserPasswordRequest request = new ResetUserPasswordRequest(
                "invalid_email"
        );
        Set<ConstraintViolation<ResetUserPasswordRequest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }
    @Test
    void invalidResetUserPasswordRequest_InvalidEmailLength(){
        ResetUserPasswordRequest request = new ResetUserPasswordRequest(
                "a@"
        );
        Set<ConstraintViolation<ResetUserPasswordRequest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }
}
