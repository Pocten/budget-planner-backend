package cz.cvut.fel.budgetplannerbackend.security.service;

import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.exceptions.InvalidCredentialsException;
import cz.cvut.fel.budgetplannerbackend.repository.UserRepository;
import cz.cvut.fel.budgetplannerbackend.security.jwt.JwtTokenProvider;
import cz.cvut.fel.budgetplannerbackend.security.model.authentication.AuthenticationRequest;
import cz.cvut.fel.budgetplannerbackend.security.model.registration.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;


    public String authenticateAndGenerateToken(AuthenticationRequest authenticationRequest) throws InvalidCredentialsException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getUserPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUserName());

        return jwtTokenProvider.generateToken(userDetails);
    }

    public User registerNewUserAccount(RegistrationRequest registrationRequest) {
        User newUser = new User();
        newUser.setUserName(registrationRequest.getUserName());
        newUser.setUserPassword(registrationRequest.getUserPassword());
        newUser.setUserEmail(registrationRequest.getUserEmail());

        return userRepository.save(newUser);
    }
}