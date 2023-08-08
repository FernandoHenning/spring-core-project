package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.dto.authentication.request.AuthenticationRequest;
import dev.fernandohenning.springcoreproject.dto.authentication.response.AuthenticationResponse;
import dev.fernandohenning.springcoreproject.dto.user.request.CreateUserRequest;
import dev.fernandohenning.springcoreproject.entity.User;
import dev.fernandohenning.springcoreproject.mapper.UserMapper;
import dev.fernandohenning.springcoreproject.model.Role;
import dev.fernandohenning.springcoreproject.model.UserDetailsImpl;
import dev.fernandohenning.springcoreproject.service.impl.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private EmailServiceImpl emailService;
    @Mock
    JwtServiceImpl jwtService;
    @Mock
    private TokenServiceImpl tokenService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final String mockAccessToken = "mockAccessToken";
    private final String mockRefreshToken = "mockRefreshToken";

    @BeforeEach
    void setUp() {

        when(jwtService.generateAccessToken(any())).thenReturn(mockAccessToken);
        when(jwtService.generateRefreshToken(any())).thenReturn(mockRefreshToken);


        User sampleUser = new User();
        sampleUser.setEmail("john.doe@example.com");
        when(userService.validateCredentials(anyString(), anyString())).thenReturn(sampleUser);


        Authentication successfulAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(successfulAuthentication);


        UserDetailsImpl userDetails = new UserDetailsImpl(sampleUser);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
    }

    @Test
    void testAuthenticate_ValidCredentials() {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "password");

        AuthenticationResponse response = authenticationService.authenticate(request);

        verify(jwtService, times(1)).generateAccessToken(any());
        verify(jwtService, times(1)).generateRefreshToken(any());

        verify(tokenService, times(1)).revokeAllUserTokens(any());
        verify(tokenService, times(1)).saveUserToken(any(), any());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(mockAccessToken, response.accessToken());
        Assertions.assertEquals(mockRefreshToken, response.refreshToken());
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "invalidPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(InternalAuthenticationServiceException.class);

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
    }
    @Test
    void testRegisterUser_ValidRequest() {

        String email = "john.doe@example.com";
        String firstName = "John";
        String lastName = "Doe";
        String password = "password";
        String passwordConfirmation = "password";
        CreateUserRequest request = new CreateUserRequest(firstName, lastName,email,password, passwordConfirmation, Role.USER);

        when(userService.emailExists(email)).thenReturn(false);
        User mockUser = new User();
        when(userMapper.toUser(request)).thenReturn(mockUser);
        when(userService.saveUser(mockUser)).thenReturn(mockUser);

        String sampleToken = "sampleToken";
        when(jwtService.generateTokenForEnableAccount(email)).thenReturn(sampleToken);

        authenticationService.registerUser(request);

        verify(emailService, times(1)).sendActivationLink(email, firstName, "http://localhost:8080/api/v1/auth/enable-user/" + sampleToken);
        verify(tokenService, times(1)).saveUserToken(mockUser, sampleToken);
    }
}
