package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.repository.DashboardAccessRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.UserRepository;
import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityAlreadyExistsException;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardAccessRepository dashboardAccessRepository;

    @Mock
    private DashboardRoleRepository dashboardRoleRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testUser");
        testUser.setUserEmail("test@example.com");
        testUser.setUserPassword("password");

        testUserDto = new UserDto(1L, "testUser", "test@example.com", "password", LocalDateTime.now());
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        // Act
        List<UserDto> users = userService.getAllUsers();

        // Assert
        assertEquals(1, users.size());
        assertEquals(testUserDto, users.get(0));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByIdWithValidId() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        // Act
        UserDto user = userService.getUserById(userId);

        // Assert
        assertNotNull(user);
        assertEquals(testUserDto, user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByIdWithNonexistentId() {
        // Arrange
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateUserSuccessfully() throws EntityAlreadyExistsException {
        // Arrange
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
        when(userRepository.findUserByUserName(anyString())).thenReturn(Optional.empty());
        when(userRepository.findUserByUserEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        // Act
        UserDto createdUser = userService.createUser(testUserDto);

        // Assert
        assertNotNull(createdUser);
        assertEquals(testUserDto, createdUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithExistingUsernameOrEmail() {
        // Arrange
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
        when(userRepository.findUserByUserName(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> userService.createUser(testUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserSuccessfully() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        UserDto updatedUserDto = new UserDto(userId, "updatedUser", "updated@example.com", "newPassword", LocalDateTime.now());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        UserDto updatedUser = userService.updateUser(userId, updatedUserDto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(testUserDto, updatedUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserWithNonexistentId() {
        // Arrange
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userId, testUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserSuccessfully() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(dashboardRepository.findAllByUserId(userId)).thenReturn(List.of());

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
        verify(dashboardAccessRepository, times(1)).deleteByUserId(userId);
        verify(dashboardRoleRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void testDeleteUserWithNonexistentId() {
        // Arrange
        Long userId = 2L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }
}