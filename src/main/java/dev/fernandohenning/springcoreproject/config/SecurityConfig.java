package dev.fernandohenning.springcoreproject.config;

import dev.fernandohenning.springcoreproject.filter.JwtAuthorizationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("access denied.");
                        }))

                .authorizeHttpRequests(authorizzeHttpRequest -> authorizzeHttpRequest
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/register/**",
                                "/api/v1/auth/refresh-token",
                                "/api/v1/auth/enable-user/**",
                                "/api/v1/auth/authenticate",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/user/**")
                        .hasAuthority("ROLE_USER")

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/admin/**")
                        .hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )

                .cors(Customizer.withDefaults())
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(
                        logout -> logout
                                .logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
                );
        return http.build();
    }

    @Bean(name = "corsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH"
                )
        );

        corsConfiguration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Expose-Headers"
                ));

        corsConfiguration.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);

        return source;
    }
}
