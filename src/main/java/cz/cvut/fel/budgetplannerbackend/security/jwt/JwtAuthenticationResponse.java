package cz.cvut.fel.budgetplannerbackend.security.jwt;

import lombok.Data;

/**
 * Represents the response sent after successful JWT authentication.
 * Contains the access token and the token type ("Bearer").
 */
@Data
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}