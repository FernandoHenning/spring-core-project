package dev.fernandohenning.springcoreproject.requests.validations.user;

import dev.fernandohenning.springcoreproject.dto.user.request.ResetUserPasswordResquest;
import dev.fernandohenning.springcoreproject.dto.user.request.UpdateUserPasswordRequest;
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
class UpdateUserPasswordRequestValidationsTests {
    private Validator validator;

    @BeforeEach
    void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUpdateUserPasswordRequest(){
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest(
                "o=bab9bC5ve4y?ZEn?Z3t!h6PgYhrc!csKMX45IDmHk7kkXPtquVLsIC9hOgCk2rEJ4",
                "password1324",
                "password1234"
        );
        Set<ConstraintViolation<UpdateUserPasswordRequest>> violations = validator.validate(request);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUpdateUserPasswordRequest_InvalidPassword(){
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest(
                "o=bab9bC5ve4y?ZEn?Z3t!h6PgYhrc!csKMX45IDmHk7kkXPtquVLsIC9hOgCk2rEJ4",
                "1234",
                "1234"
        );
        Set<ConstraintViolation<UpdateUserPasswordRequest>> violations = validator.validate(request);
        Assertions.assertFalse(violations.isEmpty());
    }
}
