package dev.fernandohenning.springcoreproject.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.fernandohenning.springcoreproject.model.Role;

import java.time.LocalDateTime;

public record GetUserInformationResponse (
        @JsonProperty("user_id")
        Long id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        Role role
) {
}
