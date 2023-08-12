package dev.fernandohenning.springcoreproject.controller;

import dev.fernandohenning.springcoreproject.dto.authentication.request.AuthenticationRequest;
import dev.fernandohenning.springcoreproject.dto.authentication.response.AuthenticationResponse;
import dev.fernandohenning.springcoreproject.dto.user.request.CreateUserRequest;
import dev.fernandohenning.springcoreproject.dto.user.request.ResetUserPasswordRequest;
import dev.fernandohenning.springcoreproject.dto.user.request.UpdateUserPasswordRequest;
import dev.fernandohenning.springcoreproject.exception.EmailAlreadyExistsException;
import dev.fernandohenning.springcoreproject.exception.PasswordDontMatchException;
import dev.fernandohenning.springcoreproject.exception.UserNotFoundException;
import dev.fernandohenning.springcoreproject.service.AuthenticationService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("register")
    public ResponseEntity<String> register(@Valid @RequestBody CreateUserRequest request) {
        try {
            authenticationService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (PasswordDontMatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords don't match");
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        } catch (MailSendException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while sending activation link");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while registering user");
        }
    }

    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("enable-user/{token}")
    public ResponseEntity<String> enableUser(@PathVariable String token) {
        try {
            authenticationService.enableUser(token);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "http://localhost:5173/login")
                    .body(null);
        } catch (ExpiredJwtException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Link has expired. Please request a new one.");
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User not found !!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to enable user.");
        }
    }

    @PostMapping("refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        AuthenticationResponse responseToken = authenticationService.refreshToken(request, response);
        return ResponseEntity.ok(responseToken);
    }

    @PostMapping("forgot-password")
    public ResponseEntity<String> sendResetPasswordRequest(@Valid @RequestBody ResetUserPasswordRequest request) {
        try {
            authenticationService.sendResetPasswordRequestToUser(request.email());
            return ResponseEntity.ok("The reset password request was sent successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Email does not exist");
        } catch (MailSendException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while sending reset password link");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody UpdateUserPasswordRequest request) {
        try {
            authenticationService.updatePassword(request.token(), request.password(), request.passwordConfirmation());
            return ResponseEntity.ok("Password updated successfully");
        } catch (PasswordDontMatchException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Passwords don't match.");
        } catch (ExpiredJwtException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Link has expired. Please request a new one.");
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User not found.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to reset password try again.");
        }
    }
}
