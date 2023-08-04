package dev.fernandohenning.springcoreproject.service.impl;

import dev.fernandohenning.springcoreproject.dto.authentication.request.AuthenticationRequest;
import dev.fernandohenning.springcoreproject.dto.authentication.response.AuthenticationResponse;
import dev.fernandohenning.springcoreproject.dto.user.request.CreateUserRequest;
import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.exception.EmailAlreadyExistsException;
import dev.fernandohenning.springcoreproject.exception.PasswordDontMatchException;
import dev.fernandohenning.springcoreproject.mapper.UserMapper;
import dev.fernandohenning.springcoreproject.model.UserDetailsImpl;
import dev.fernandohenning.springcoreproject.service.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserMapper userMapper;


    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()));
        } catch (InternalAuthenticationServiceException e) {
            log.error("Error while authenticating user with request {}", request);
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = userService.validateCredentials(request.email(), request.password());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, accessToken);

        return new AuthenticationResponse(accessToken, refreshToken);

    }

    @Override
    public void registerUser(CreateUserRequest request) {
        if (!passwordAndPasswordConfirmationMatches(request)) throw new PasswordDontMatchException();
        if (userService.emailExists(request.email())) throw new EmailAlreadyExistsException();

        User user = userMapper.toUser(request);
        User savedUser = userService.saveUser(user);
        String jwtToken = jwtService.generateTokenForEnableAccount(user.getEmail());
        String activationLink = "http://localhost:8080/api/v1/auth/enable-user/" + jwtToken;

        try {
            emailService.sendActivationLink(request.email(), request.firstName(), activationLink);
        } catch (Exception e) {
            throw new MailSendException("Error while sending activation link to user with email :" + request.email());
        }

        tokenService.saveUserToken(savedUser, jwtToken);
    }

    @Override
    public void sendResetPasswordRequestToUser(String email) {
        User user = userService.findUserByEmail(email);
        String jwtToken = jwtService.generateTokenForResetPassword(user.getEmail());
        String resetPasswordLink = "http://localhost:5173/reset-password?token=" + jwtToken;
        try {
            emailService.sendResetPasswordRequest(email, user.getFirstName(), resetPasswordLink);
        } catch (Exception e) {
            throw new MailSendException("Error while sending reset password link to user with email :" + email);
        }
    }

    @Override
    public void updatePassword(String token, String password, String passwordConfirmation) {
        String email = jwtService.extractUsername(token);
        userService.updatePassword(email, password, passwordConfirmation);
    }

    @Override
    public void enableUser(String token) {
        String email = jwtService.extractUsername(token);
        userService.enableUser(email);
    }

    @Override
    public boolean passwordAndPasswordConfirmationMatches(CreateUserRequest request) {
        return request.password().equals(request.passwordConfirmation());
    }

    @Override
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuthenticationResponse result = null;

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header.");
        } else {
            try {
                final String refreshToken = authHeader.substring(7);
                var username = jwtService.extractUsername(refreshToken);

                if (username != null) {

                    UserDetailsImpl userDetails = (UserDetailsImpl) userService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(refreshToken, userDetails)) {

                        var accessToken = jwtService.generateAccessToken(userDetails.user());
                        log.info("Access token is {}", accessToken);
                        tokenService.revokeAllUserTokens(userDetails.user());
                        tokenService.saveUserToken(userDetails.user(), accessToken);
                        result = new AuthenticationResponse(accessToken, refreshToken);
                    }
                }
            } catch (ExpiredJwtException ex) {
                response.sendError(SC_UNAUTHORIZED, "refresh token expired");
            } catch (MalformedJwtException e) {
                log.warn("refresh token expired: {}", e.getMessage());
                response.sendError(SC_UNAUTHORIZED, "invalid refresh token.");
            }
        }
        return result;
    }
}
