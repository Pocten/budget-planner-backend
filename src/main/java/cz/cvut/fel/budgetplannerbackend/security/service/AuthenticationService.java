package cz.cvut.fel.budgetplannerbackend.security.service;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.exceptions.InvalidCredentialsException;
import cz.cvut.fel.budgetplannerbackend.mapper.UserMapper;
import cz.cvut.fel.budgetplannerbackend.security.jwt.JwtTokenProvider;
import cz.cvut.fel.budgetplannerbackend.security.model.authentication.AuthenticationRequest;
import cz.cvut.fel.budgetplannerbackend.security.model.registration.RegistrationRequest;
import cz.cvut.fel.budgetplannerbackend.service.UserService;
import lombok.RequiredArgsConstructor;
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
    private final UserService userService;
    private final UserMapper userMapper;


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
        UserDto userDto = new UserDto(
                null,
                registrationRequest.getUserName(),
                registrationRequest.getUserEmail(),
                registrationRequest.getUserPassword(),
                LocalDateTime.now(),
                null);
        UserDto createdUserDto = userService.createUser(userDto);
        return userMapper.toEntity(createdUserDto);
    }
}