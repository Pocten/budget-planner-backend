package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityAlreadyExistsException;
import cz.cvut.fel.budgetplannerbackend.exceptions.InvalidCredentialsException;
import cz.cvut.fel.budgetplannerbackend.security.model.authentication.AuthenticationRequest;
import cz.cvut.fel.budgetplannerbackend.security.model.authentication.AuthenticationResponse;
import cz.cvut.fel.budgetplannerbackend.security.model.registration.RegistrationRequest;
import cz.cvut.fel.budgetplannerbackend.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user authentication and registration.
 * Provides endpoints for user login and registration.
 */
@RestController
@RequestMapping("/api/auth") // Base URL for authentication-related endpoints.
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService; // Service for authentication and registration operations.
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    /**
     * Handles user login requests.
     *
     * @param authenticationRequest The authentication request containing the user's username and password.
     * @return A ResponseEntity containing an AuthenticationResponse with a JWT token if successful,
     *         or an appropriate error response if authentication fails.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        LOG.info("Login request for user: {}", authenticationRequest.getUserName());
        try {
            // Authenticate the user and generate a JWT token.
            String jwt = authenticationService.authenticateAndGenerateToken(authenticationRequest);
            LOG.info("User logged in: {}", authenticationRequest.getUserName());
            return ResponseEntity.ok(new AuthenticationResponse(jwt)); // Return the JWT token in the response.
        } catch (InvalidCredentialsException e) {
            // If authentication fails due to invalid credentials, log the error and re-throw the exception.
            LOG.error("Login failed for user: {}", authenticationRequest.getUserName(), e);
            throw e;
        }
    }

    /**
     * Handles user registration requests.
     *
     * @param registrationRequest The registration request containing the new user's details.
     * @return A ResponseEntity containing the newly created User object and an HTTP status of 200 OK if successful,
     *         or an appropriate error response if registration fails.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUserAccount(@RequestBody RegistrationRequest registrationRequest) {
        LOG.info("Registration request for user: {}", registrationRequest.getUserName());
        try {
            // Register the new user account.
            User registered = authenticationService.registerNewUserAccount(registrationRequest);
            LOG.info("User registered: {}", registrationRequest.getUserName());
            return ResponseEntity.ok(registered); // Return the registered User object with an OK status.
        } catch (EntityAlreadyExistsException e) {
            // If a user with the same username or email already exists, log the error and return a Conflict status.
            LOG.error("Registration failed for user: {}", registrationRequest.getUserName(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            // For any other exceptions, log the error and return a Bad Request status.
            LOG.error("Registration error for user: {}", registrationRequest.getUserName(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}