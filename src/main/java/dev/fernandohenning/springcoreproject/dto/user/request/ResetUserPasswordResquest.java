package dev.fernandohenning.springcoreproject.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record ResetUserPasswordResquest(
        @Email(message = "Email should be valid.")
        @NotNull(message = "Email shouldn't be null.")
        @Length(min = 3, message = "Email length should be more than 3.")
        String email
) {
}
