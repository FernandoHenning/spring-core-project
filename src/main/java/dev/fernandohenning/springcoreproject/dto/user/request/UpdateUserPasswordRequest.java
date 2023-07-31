package dev.fernandohenning.springcoreproject.dto.user.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateUserPasswordRequest(
        @NotNull
        String token,
        @NotNull(message = "Paasword shouldn't be null.")
        @Length(min = 8, max = 16, message = "Password lenght shoul be more than 8 and less than 16 characters.")
        String password,
        @NotNull(message = "Password confirmation shouldn't be null.")
        String passwordConfirmation
) {
}
