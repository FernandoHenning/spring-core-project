package dev.fernandohenning.springcoreproject.requests.validations.user;

import dev.fernandohenning.springcoreproject.dto.user.request.ResetUserPasswordResquest;
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
        ResetUserPasswordResquest request = new ResetUserPasswordResquest(
                "test@tes.com"
        );
        Set<ConstraintViolation<ResetUserPasswordResquest>> violations = validator.validate(request);
        Assertions.assertTrue(violations.isEmpty());
    }
    @Test
    void invalidResetUserPasswordRequest_InvalidEmail(){
        ResetUserPasswordResquest request = new ResetUserPasswordResquest(
                "invalid_email"
        );
        Set<ConstraintViolation<ResetUserPasswordResquest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }
    @Test
    void invalidResetUserPasswordRequest_InvalidEmailLength(){
        ResetUserPasswordResquest request = new ResetUserPasswordResquest(
                "a@"
        );
        Set<ConstraintViolation<ResetUserPasswordResquest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }
}
