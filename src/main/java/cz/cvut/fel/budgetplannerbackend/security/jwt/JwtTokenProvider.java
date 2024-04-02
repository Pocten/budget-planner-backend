package cz.cvut.fel.budgetplannerbackend.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import cz.cvut.fel.budgetplannerbackend.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getTokenExpiry());

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("userId", customUserDetails.getUserId())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC512(jwtProperties.getSecret()));
    }

    public String getUsernameFromJWT(String token) {
        return JWT.require(Algorithm.HMAC512(jwtProperties.getSecret()))
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(jwtProperties.getSecret())).build().verify(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}