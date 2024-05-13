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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        LOG.info("Login request for user: {}", authenticationRequest.getUserName());
        try {
            String jwt = authenticationService.authenticateAndGenerateToken(authenticationRequest);
            LOG.info("User logged in: {}", authenticationRequest.getUserName());
            return ResponseEntity.ok(new AuthenticationResponse(jwt));
        } catch (InvalidCredentialsException e) {
            LOG.error("Login failed for user: {}", authenticationRequest.getUserName(), e);
            throw e; // or handle the exception and return an appropriate ResponseEntity
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUserAccount(@RequestBody RegistrationRequest registrationRequest) {
        LOG.info("Registration request for user: {}", registrationRequest.getUserName());
        try {
            User registered = authenticationService.registerNewUserAccount(registrationRequest);
            LOG.info("User registered: {}", registrationRequest.getUserName());
            return ResponseEntity.ok(registered);
        } catch (EntityAlreadyExistsException e) {
            LOG.error("Registration failed for user: {}", registrationRequest.getUserName(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            LOG.error("Registration error for user: {}", registrationRequest.getUserName(), e);
            return ResponseEntity.badRequest().build(); // or another appropriate status based on the error
        }
    }
}