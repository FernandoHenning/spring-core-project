package dev.fernandohenning.springcoreproject.filter;

import dev.fernandohenning.springcoreproject.model.UserDetailsImpl;
import dev.fernandohenning.springcoreproject.service.JwtService;
import dev.fernandohenning.springcoreproject.service.TokenService;
import dev.fernandohenning.springcoreproject.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * This class is a Spring component that extends OncePerRequestFilter, which is a convenient base class for filter
 * implementations. It filters incoming requests and checks for a valid JWT in the Authorization header.
 * If a valid JWT is found, it authenticates the user associated with the token.
 * If the token is absent or not valid, the request is rejected.
 * <p>
 * The component has three dependencies: JwtService, UserService, and TokenService.
 */
@Component
@Slf4j
@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final TokenService tokenService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/enable-user",
            "/api/v1/auth/authenticate",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if (PUBLIC_ENDPOINTS.contains(servletPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            processTokenAuthentication(request, response, filterChain, authHeader.substring(BEARER_PREFIX.length()));
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void processTokenAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String jwt) throws IOException{
        try {
            String username = jwtService.extractUsername(jwt);
            UserDetailsImpl userDetails = (UserDetailsImpl) userService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails) && tokenService.isTokenValid(jwt)) {
                setAuthenticationToSecurityContextHolder(request, userDetails);
                filterChain.doFilter(request, response);
                return;
            }

            throw new ServletException("Access denied");
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException ex) {
            handleJwtException(response, ex);
        } catch (UsernameNotFoundException ex) {
            handleUsernameNotFoundException(response);
        } catch (Exception ex) {
            handleInternalServerError(response);
        }
    }

    private void setAuthenticationToSecurityContextHolder(HttpServletRequest request, UserDetailsImpl userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void handleJwtException(HttpServletResponse response, Exception ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("JWT Error: " + ex.getMessage());
    }

    private void handleUsernameNotFoundException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("User not found");
    }

    private void handleInternalServerError(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
}
