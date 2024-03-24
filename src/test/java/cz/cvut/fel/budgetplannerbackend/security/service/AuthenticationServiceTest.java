package cz.cvut.fel.budgetplannerbackend.security.service;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.mapper.UserMapper;
import cz.cvut.fel.budgetplannerbackend.security.jwt.JwtTokenProvider;
import cz.cvut.fel.budgetplannerbackend.security.model.authentication.AuthenticationRequest;
import cz.cvut.fel.budgetplannerbackend.security.model.registration.RegistrationRequest;
import cz.cvut.fel.budgetplannerbackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticateAndGenerateTokenTest() {
        // Arrange
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("testUser", "testPassword");
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        String expectedToken = "testToken";

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn(expectedToken);

        // Act
        String result = authenticationService.authenticateAndGenerateToken(authenticationRequest);

        // Assert
        assertEquals(expectedToken, result);
        Mockito.verify(authenticationManager).authenticate(any());
        Mockito.verify(jwtTokenProvider).generateToken(any(UserDetails.class));
    }

    @Test
    void registerNewUserAccountTest() {
        // Arrange
        RegistrationRequest registrationRequest = new RegistrationRequest("testUser", "testEmail", "testPassword");
        UserDto userDto = new UserDto(null, "testUser", "testEmail", "testPassword", null, null);
        User user = new User();
        user.setUserName("testUser");

        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);

        // Act
        User result = authenticationService.registerNewUserAccount(registrationRequest);

        // Assert
        assertEquals("testUser", result.getUserName());
        Mockito.verify(userService).createUser(any(UserDto.class));
        Mockito.verify(userMapper).toEntity(any(UserDto.class));
    }
}