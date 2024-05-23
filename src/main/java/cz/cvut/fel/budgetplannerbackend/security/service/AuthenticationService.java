package cz.cvut.fel.budgetplannerbackend.security.service;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityAlreadyExistsException;
import cz.cvut.fel.budgetplannerbackend.exceptions.InvalidCredentialsException;
import cz.cvut.fel.budgetplannerbackend.mapper.UserMapper;
import cz.cvut.fel.budgetplannerbackend.security.jwt.JwtTokenProvider;
import cz.cvut.fel.budgetplannerbackend.security.model.authentication.AuthenticationRequest;
import cz.cvut.fel.budgetplannerbackend.security.model.registration.RegistrationRequest;
import cz.cvut.fel.budgetplannerbackend.service.implementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service class for handling user authentication and registration.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    /**
     * Authenticates a user and generates a JWT token if authentication is successful.
     *
     * @param authenticationRequest The authentication request containing username and password.
     * @return The generated JWT token as a string.
     * @throws InvalidCredentialsException If the provided credentials are invalid (incorrect username or password).
     */
    public String authenticateAndGenerateToken(AuthenticationRequest authenticationRequest) throws InvalidCredentialsException {
        LOG.info("Authenticating user {}", authenticationRequest.getUserName());
        try {
            // Authenticate the user using Spring Security's AuthenticationManager.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getUserPassword())
            );
        } catch (BadCredentialsException e) {
            // Log the authentication failure and throw an InvalidCredentialsException.
            LOG.error("Authentication failed for user {}", authenticationRequest.getUserName(), e);
            throw new InvalidCredentialsException("Incorrect username or password", e);
        }

        // If authentication is successful, load user details and generate a JWT token.
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUserName());

        String token = jwtTokenProvider.generateToken(userDetails);
        LOG.info("Generated JWT token for user {}", authenticationRequest.getUserName());
        return token; // Return the generated JWT token.
    }

    /**
     * Registers a new user account.
     *
     * @param registrationRequest The registration request containing user information.
     * @return The newly created User entity.
     * @throws EntityAlreadyExistsException If a user with the same username or email already exists.
     */
    public User registerNewUserAccount(RegistrationRequest registrationRequest) {
        LOG.info("Registering new user account for {}", registrationRequest.getUserName());

        // Create a UserDto object from the registration request data.
        UserDto userDto = new UserDto(
                null,
                registrationRequest.getUserName(),
                registrationRequest.getUserEmail(),
                registrationRequest.getUserPassword(),
                LocalDateTime.now());

        UserDto createdUserDto;
        try {
            // Create a new user using the userService.
            createdUserDto = userService.createUser(userDto);
            LOG.info("User account created for {}", registrationRequest.getUserName());
        } catch (EntityAlreadyExistsException e) {
            // If a user already exists, log the error and re-throw the exception.
            LOG.error("Registration failed for user {}. User already exists.", registrationRequest.getUserName(), e);
            throw e;
        }

        // Convert the created UserDto to a User entity and return it.
        return userMapper.toEntity(createdUserDto);
    }
}