package dev.fernandohenning.springcoreproject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfigProperties {
        private String protocol;
        private String port;
        private String host;
        private String username;
        private String password;
}
