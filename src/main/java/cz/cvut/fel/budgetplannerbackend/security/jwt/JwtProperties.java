package cz.cvut.fel.budgetplannerbackend.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long tokenExpiry;
    private long refreshTokenExpiry;
    private String tokenPrefix;
    private String headerString;

    // getters и setters
}
