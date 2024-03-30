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

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    public String authenticateAndGenerateToken(AuthenticationRequest authenticationRequest) throws InvalidCredentialsException {
        LOG.info("Authenticating user {}", authenticationRequest.getUserName());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getUserPassword())
            );
        }
        catch (BadCredentialsException e) {
            LOG.error("Authentication failed for user {}", authenticationRequest.getUserName(), e);
            throw new InvalidCredentialsException("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUserName());

        String token = jwtTokenProvider.generateToken(userDetails);
        LOG.info("Generated JWT token for user {}", authenticationRequest.getUserName());
        return token;
    }

    public User registerNewUserAccount(RegistrationRequest registrationRequest) {
        LOG.info("Registering new user account for {}", registrationRequest.getUserName());
        UserDto userDto = new UserDto(
                null,
                registrationRequest.getUserName(),
                registrationRequest.getUserEmail(),
                registrationRequest.getUserPassword(),
                LocalDateTime.now(),
                null);
        UserDto createdUserDto;
        try {
            createdUserDto = userService.createUser(userDto);
            LOG.info("User account created for {}", registrationRequest.getUserName());
        } catch (EntityAlreadyExistsException e) {
            LOG.error("Registration failed for user {}. User already exists.", registrationRequest.getUserName(), e);
            throw e;
        }

        return userMapper.toEntity(createdUserDto);
    }
}