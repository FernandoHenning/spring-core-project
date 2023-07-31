package dev.fernandohenning.springcoreproject.dto.user.request;

import dev.fernandohenning.springcoreproject.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateUserRequest(
        @Length(min = 3, max = 16,
                message = "First name length should be less than 16 and more than 3 characters.")
        String firstName,
        @Length(min = 3, max = 16,
                message = "Last name length should be less than 16 and more than 3 characters.")
        String lastName,
        @Email(message = "Email should be valid.")
        @NotNull(message = "Email shouldn't be null.")
        @Length(min = 3,
                message = "Email length should be more than 3")
        String email,
        @NotNull(message = "Password shouldn't be null.")
        @Length(min = 8, max = 16,
                message = "password lenght should be more than 8 and less than 16 characters.")
        String password,
        @NotNull
        String passwordConfirmation,
        Role role
) {
}
