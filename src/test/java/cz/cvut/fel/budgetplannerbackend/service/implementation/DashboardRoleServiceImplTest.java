package cz.cvut.fel.budgetplannerbackend.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cz.cvut.fel.budgetplannerbackend.entity.*;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DashboardRoleServiceImplTest {

    @Mock
    private DashboardRoleRepository dashboardRoleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DashboardRoleServiceImpl dashboardRoleService;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardRoleServiceImplTest.class);

    private User testUser;
    private Dashboard testDashboard;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testDashboard = new Dashboard();
        testDashboard.setId(1L);

        testRole = new Role();
        testRole.setName(ERole.NONE);
    }

    @Test
    void testAssignRoleToUserInDashboard_SuccessfullyAssignsNewRole() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(dashboardRepository.findById(testDashboard.getId())).thenReturn(Optional.of(testDashboard));
        when(roleRepository.findByName(ERole.NONE)).thenReturn(Optional.of(testRole));
        when(dashboardRoleRepository.findByUserIdAndDashboardId(testUser.getId(), testDashboard.getId())).thenReturn(Optional.empty());

        // Act
        dashboardRoleService.assignRoleToUserInDashboard(testUser.getId(), testDashboard.getId(), ERole.NONE);

        // Assert
        verify(dashboardRoleRepository, times(1)).save(any(DashboardRole.class));
        verify(dashboardRoleRepository, times(1)).findByUserIdAndDashboardId(testUser.getId(), testDashboard.getId());
    }

    @Test
    void testAssignRoleToUserInDashboard_UpdatesExistingRole() {
        // Arrange
        DashboardRole existingRole = new DashboardRole(null, testUser, testDashboard, testRole);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(dashboardRepository.findById(testDashboard.getId())).thenReturn(Optional.of(testDashboard));
        when(roleRepository.findByName(ERole.NONE)).thenReturn(Optional.of(testRole));
        when(dashboardRoleRepository.findByUserIdAndDashboardId(testUser.getId(), testDashboard.getId())).thenReturn(Optional.of(existingRole));

        // Act
        dashboardRoleService.assignRoleToUserInDashboard(testUser.getId(), testDashboard.getId(), ERole.NONE);

        // Assert
        verify(dashboardRoleRepository, times(1)).save(existingRole);
    }

    @Test
    void testAssignRoleToUserInDashboard_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            dashboardRoleService.assignRoleToUserInDashboard(testUser.getId(), testDashboard.getId(), ERole.NONE);
        });
        assertEquals("User with id 1 not found", exception.getMessage());
    }

    @Test
    void testAssignRoleToUserInDashboard_DashboardNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(dashboardRepository.findById(testDashboard.getId())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            dashboardRoleService.assignRoleToUserInDashboard(testUser.getId(), testDashboard.getId(), ERole.NONE);
        });
        assertEquals("Dashboard with id 1 not found", exception.getMessage());
    }

    @Test
    void testAssignRoleToUserInDashboard_RoleNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(dashboardRepository.findById(testDashboard.getId())).thenReturn(Optional.of(testDashboard));
        when(roleRepository.findByName(ERole.NONE)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            dashboardRoleService.assignRoleToUserInDashboard(testUser.getId(), testDashboard.getId(), ERole.NONE);
        });
        assertEquals("Role with id NONE not found", exception.getMessage());
    }
}
