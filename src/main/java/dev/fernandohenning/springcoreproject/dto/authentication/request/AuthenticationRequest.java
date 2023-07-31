package dev.fernandohenning.springcoreproject.dto.authentication.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record AuthenticationRequest(
        @Email(message = "Email should be valid.")
        @NotNull(message = "Email shouldn't be null.")
        @Length(min = 3,
                message = "Email length should be more than 3")
        String email,
        @NotNull(message = "Password shouldn't be null.")
        @Length(min = 8, max = 16,
                message = "password lenght should be more than 8 and less than 16 characters.")
        String password
) {
}
