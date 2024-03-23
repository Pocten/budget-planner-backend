package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.security.model.authentication.AuthenticationRequest;
import cz.cvut.fel.budgetplannerbackend.security.model.authentication.AuthenticationResponse;
import cz.cvut.fel.budgetplannerbackend.security.model.registration.RegistrationRequest;
import cz.cvut.fel.budgetplannerbackend.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        String jwt = authenticationService.authenticateAndGenerateToken(authenticationRequest);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUserAccount(@RequestBody RegistrationRequest registrationRequest) {
        User registered = authenticationService.registerNewUserAccount(registrationRequest);
        return ResponseEntity.ok(registered);
    }
}