package cz.cvut.fel.budgetplannerbackend.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import cz.cvut.fel.budgetplannerbackend.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Provides methods for generating, validating, and extracting information from JWT tokens.
 */
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Generates a JWT token for the given UserDetails.
     *
     * @param userDetails The UserDetails object containing user information.
     * @return The generated JWT token as a String.
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getTokenExpiry());

        // Cast UserDetails to CustomUserDetails to access userId.
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        // Create the JWT token using the provided algorithm, subject, claims, and expiry date.
        return JWT.create()
                .withSubject(userDetails.getUsername()) // Set the username as the subject.
                .withClaim("userId", customUserDetails.getUserId()) // Add a custom claim for userId.
                .withIssuedAt(now) // Set the issued at timestamp.
                .withExpiresAt(expiryDate) // Set the expiration date.
                .sign(Algorithm.HMAC512(jwtProperties.getSecret())); // Sign the token with the secret key.
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token string.
     * @return The username extracted from the token.
     */
    public String getUsernameFromJWT(String token) {
        // Verify the token using the secret key and extract the subject (username).
        return JWT.require(Algorithm.HMAC512(jwtProperties.getSecret()))
                .build()
                .verify(token)
                .getSubject();
    }

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token string.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            // Attempt to verify the token using the secret key.
            JWT.require(Algorithm.HMAC512(jwtProperties.getSecret())).build().verify(token);
            return true; // Token is valid.
        } catch (Exception ex) {
            // Any exception during verification indicates an invalid token.
            return false;
        }
    }
}