package dev.fernandohenning.springcoreproject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigProperties {

    private long accessTokenExpiration;
    private long refreshTokenExpiration;
    private long resetPasswordExpiration;
    private long enableAccountExpiration;
    private String secretKey;
}
