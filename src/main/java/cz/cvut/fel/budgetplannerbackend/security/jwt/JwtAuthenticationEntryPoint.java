package cz.cvut.fel.budgetplannerbackend.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for handling authentication failures in the JWT authentication process.
 * This class is responsible for sending a 401 Unauthorized response with a JSON body
 * containing details of the authentication error.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences the authentication process when an AuthenticationException is thrown.
     *
     * @param request The HttpServletRequest that resulted in an AuthenticationException.
     * @param response The HttpServletResponse to be used to send the response.
     * @param authException The AuthenticationException that caused the authentication failure.
     * @throws IOException If an I/O error occurs while writing the response.
     * @throws ServletException If a servlet-specific error occurs.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // Set the response content type to JSON and the status to 401 Unauthorized.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a map to store the error details.
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage()); // Include the exception message.
        body.put("path", request.getServletPath()); // Include the request path.

        // Use Jackson's ObjectMapper to serialize the error details to JSON and write them to the response output stream.
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}