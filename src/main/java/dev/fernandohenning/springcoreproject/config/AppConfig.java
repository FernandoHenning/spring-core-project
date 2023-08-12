package dev.fernandohenning.springcoreproject.config;

import dev.fernandohenning.springcoreproject.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserServiceImpl userServiceImpl;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    /**
     * Configures a UserDetailsService that uses the injected UserService to find a user by email.
     *
     * @return UserDetailsService instance
     */
    @Bean
    @Primary
    public UserDetailsService userDetailsService(){
        return userServiceImpl;
    }
    /**
     * Configures an AuthenticationProvider that uses the userDetailsService and passwordEncoder to authenticate users.
     *
     * @return AuthenticationProvider instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    /**
     * Configures a BCryptPasswordEncoder instance to encode passwords.
     * This method is static to avoid the 'Relying upon circular' problem.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures an AuthenticationManager using the AuthenticationConfiguration instance.
     *
     * @param config the AuthenticationConfiguration instance
     * @return AuthenticationManager instance
     * @throws Exception if there is an error configuring the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
