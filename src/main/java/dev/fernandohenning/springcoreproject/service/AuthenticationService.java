package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.dto.authentication.request.AuthenticationRequest;
import dev.fernandohenning.springcoreproject.dto.authentication.response.AuthenticationResponse;
import dev.fernandohenning.springcoreproject.dto.user.request.CreateUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void registerUser(CreateUserRequest request);
    void sendResetPasswordRequestToUser(String email);
    void updatePassword(String token, String password, String passwordConfirmation);
    void enableUser(String token);
    boolean passwordAndPasswordConfirmationMatches(CreateUserRequest request);
    AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
