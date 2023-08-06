package dev.fernandohenning.springcoreproject.service;

import dev.fernandohenning.springcoreproject.service.impl.LogoutServiceImpl;
import dev.fernandohenning.springcoreproject.service.impl.TokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTests {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;

    @Mock
    private TokenServiceImpl tokenService;

    @InjectMocks
    private LogoutServiceImpl logoutService;

    @Test
    void testLogout_WithValidAuthorizationHeader_ShouldRevokeToken() {
        String validJwt = "validJwtToken";
        String authHeader = "Bearer " + validJwt;

        given(request.getHeader("Authorization")).willReturn(authHeader);

        logoutService.logout(request, response, authentication);

        verify(tokenService, times(1)).revokeToken(validJwt);
    }

    @Test
    void testLogout_WithInvalidAuthorizationHeader_ShouldNotRevokeToken() {
        String invalidAuthHeader = "InvalidHeader";

        given(request.getHeader("Authorization")).willReturn(invalidAuthHeader);

        logoutService.logout(request, response, authentication);

        verify(tokenService, never()).revokeToken(any());
    }

    @Test
    void testLogout_WithNullAuthorizationHeader_ShouldNotRevokeToken() {

        when(request.getHeader("Authorization")).thenReturn(null);

        logoutService.logout(request, response, authentication);

        verify(tokenService, never()).revokeToken(any());
    }
}
