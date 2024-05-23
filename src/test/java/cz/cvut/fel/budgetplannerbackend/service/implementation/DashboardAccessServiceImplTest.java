package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.entity.AccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.DashboardAccess;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.repository.AccessLevelRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardAccessRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardAccessServiceImplTest {

    @Mock
    private DashboardAccessRepository dashboardAccessRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private AccessLevelRepository accessLevelRepository;

    @InjectMocks
    private DashboardAccessServiceImpl dashboardAccessService;

    private User testUser;
    private Dashboard testDashboard;
    private AccessLevel testAccessLevel;
    private DashboardAccess testDashboardAccess;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testUser");

        testDashboard = new Dashboard();
        testDashboard.setId(1L);

        testAccessLevel = new AccessLevel();
        testAccessLevel.setLevel(EAccessLevel.VIEWER);

        testDashboardAccess = new DashboardAccess();
        testDashboardAccess.setUser(testUser);
        testDashboardAccess.setDashboard(testDashboard);
        testDashboardAccess.setAccessLevel(testAccessLevel);
    }

    @Test
    void testGrantAccess_NewAccess() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(dashboardRepository.findById(testDashboard.getId())).thenReturn(Optional.of(testDashboard));
        when(accessLevelRepository.findByLevel(EAccessLevel.VIEWER)).thenReturn(Optional.of(testAccessLevel));
        when(dashboardAccessRepository.findByUserIdAndDashboardId(testUser.getId(), testDashboard.getId())).thenReturn(Optional.empty());

        // Act
        dashboardAccessService.grantAccess(testUser.getId(), testDashboard.getId(), EAccessLevel.VIEWER);

        // Assert
        verify(dashboardAccessRepository, times(1)).findByUserIdAndDashboardId(testUser.getId(), testDashboard.getId());
        verify(dashboardAccessRepository, times(1)).save(any(DashboardAccess.class));
    }

    @Test
    void testGrantAccess_UpdateAccess() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(dashboardRepository.findById(testDashboard.getId())).thenReturn(Optional.of(testDashboard));
        when(accessLevelRepository.findByLevel(EAccessLevel.VIEWER)).thenReturn(Optional.of(testAccessLevel));
        when(dashboardAccessRepository.findByUserIdAndDashboardId(testUser.getId(), testDashboard.getId())).thenReturn(Optional.of(testDashboardAccess));

        // Act
        dashboardAccessService.grantAccess(testUser.getId(), testDashboard.getId(), EAccessLevel.VIEWER);

        // Assert
        verify(dashboardAccessRepository, times(1)).findByUserIdAndDashboardId(testUser.getId(), testDashboard.getId());
        verify(dashboardAccessRepository, times(1)).save(testDashboardAccess);
    }

    @Test
    void testGrantAccess_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardAccessService.grantAccess(testUser.getId(), testDashboard.getId(), EAccessLevel.VIEWER));
        verify(dashboardAccessRepository, never()).save(any(DashboardAccess.class));
    }

    @Test
    void testGrantAccess_DashboardNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(dashboardRepository.findById(testDashboard.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardAccessService.grantAccess(testUser.getId(), testDashboard.getId(), EAccessLevel.VIEWER));
        verify(dashboardAccessRepository, never()).save(any(DashboardAccess.class));
    }

    @Test
    void testGrantAccess_AccessLevelNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(dashboardRepository.findById(testDashboard.getId())).thenReturn(Optional.of(testDashboard));
        when(accessLevelRepository.findByLevel(EAccessLevel.VIEWER)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardAccessService.grantAccess(testUser.getId(), testDashboard.getId(), EAccessLevel.VIEWER));
        verify(dashboardAccessRepository, never()).save(any(DashboardAccess.class));
    }

    @Test
    void testGetAccessibleDashboardIds() {
        // Arrange
        when(dashboardAccessRepository.findAllByUserId(testUser.getId())).thenReturn(List.of(testDashboardAccess));

        // Act
        List<Long> accessibleDashboardIds = dashboardAccessService.getAccessibleDashboardIds(testUser.getId());

        // Assert
        assertEquals(1, accessibleDashboardIds.size());
        assertEquals(testDashboard.getId(), accessibleDashboardIds.get(0));
        verify(dashboardAccessRepository, times(1)).findAllByUserId(testUser.getId());
    }
}
