package cz.cvut.fel.budgetplannerbackend.unitTests.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.mapper.DashboardMapper;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import cz.cvut.fel.budgetplannerbackend.service.implementation.DashboardServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private DashboardMapper dashboardMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testUser");

        Dashboard testDashboard = new Dashboard();
        testDashboard.setId(1L);
        testDashboard.setUser(testUser);

        DashboardDto testDashboardDto = new DashboardDto(
                1L,                          // id
                "Sample Dashboard",             // title
                "This is a sample dashboard.",  // description
                LocalDateTime.now(),            // dateCreated
                1L                              // userId
        );

        // Мокирование getCurrentUser
        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        // Мокирование findAllByUserId
        when(dashboardRepository.findAllByUserId(anyLong())).thenReturn(List.of(testDashboard));
        // Мокирование toDto
        when(dashboardMapper.toDto(any(Dashboard.class))).thenReturn(testDashboardDto);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(dashboardRepository, dashboardMapper, securityUtils);
    }

    @Test
    void testFindAllDashboardsByUserIdWithValidUser() {
        // Arrange
        Long userId = 1L;
        Dashboard dashboard = new Dashboard();
        dashboard.setId(1L);
        dashboard.setTitle("Sample Dashboard");
        List<Dashboard> expectedDashboards = Collections.singletonList(dashboard);

        when(dashboardRepository.findAllByUserId(userId)).thenReturn(expectedDashboards);
        doNothing().when(securityUtils).checkAuthenticatedUser(userId);

        // Act
        List<DashboardDto> actualDashboards = dashboardService.findAllDashboardsByUserId(userId);

        // Assert
        assertEquals(1, actualDashboards.size(), "The size of the dashboard list should be 1");
        assertEquals(dashboard.getTitle(), actualDashboards.get(0).title(), "Titles of dashboards should match");
        verify(securityUtils, times(1)).checkAuthenticatedUser(userId);
    }

    @Test
    void testFindAllDashboardsByUserIdWithUnauthorizedUser() {
        // Arrange
        Long userId = 2L;
        when(dashboardRepository.findAllByUserId(userId)).thenThrow(new SecurityException("User not authorized to view these dashboards"));

        // Act & Assert
        try {
            dashboardService.findAllDashboardsByUserId(userId);
        } catch (SecurityException ex) {
            assertEquals("User not authorized to view these dashboards", ex.getMessage(), "Exception message should match expected message");
        }

        verify(securityUtils, times(1)).checkAuthenticatedUser(userId);
    }

    // Tests for the method 'findUserDashboardById'

    @Test
    void testFindUserDashboardByIdWithValidId() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testFindUserDashboardByIdWithUnauthorizedUser() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testFindUserDashboardByIdWithNonexistentId() {
        // Arrange

        // Act

        // Assert
    }

    // Tests for the method 'findDashboardById'

    @Test
    void testFindDashboardByIdWithValidId() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testFindDashboardByIdWithNonexistentId() {
        // Arrange

        // Act

        // Assert
    }

    // Tests for the method 'findAccessibleDashboards'

    @Test
    void testFindAccessibleDashboardsWithAccess() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testFindAccessibleDashboardsWithNoAccess() {
        // Arrange

        // Act

        // Assert
    }

    // Tests for the method 'createDashboard'

    @Test
    void testCreateDashboardSuccessfully() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testCreateDashboardWithUnauthorizedUser() {
        // Arrange

        // Act

        // Assert
    }

    // Tests for the method 'updateDashboard'

    @Test
    void testUpdateDashboardSuccessfully() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testUpdateDashboardWithNonexistentId() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testUpdateDashboardWithUnauthorizedAccess() {
        // Arrange

        // Act

        // Assert
    }

    // Tests for the method 'deleteDashboard'

    @Test
    void testDeleteDashboardSuccessfully() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testDeleteDashboardWithNonexistentId() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testDeleteDashboardWithUnauthorizedUser() {
        // Arrange

        // Act

        // Assert
    }

    // Tests for the method 'addMember'

    @Test
    void testAddMemberSuccessfully() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testAddMemberWithNonexistentDashboard() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testAddMemberWithUnauthorizedUser() {
        // Arrange

        // Act

        // Assert
    }


    // Tests for the method 'findMembersByDashboardId'

    @Test
    void testFindMembersByDashboardIdSuccessfully() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testFindMembersByDashboardIdWithNonexistentDashboard() {
        // Arrange

        // Act

        // Assert
    }

    // Tests for the method 'changeAccessLevel'

    @Test
    void testChangeAccessLevelSuccessfully() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testChangeAccessLevelWithUnauthorizedUser() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testChangeAccessLevelToOwner() {
        // Arrange

        // Act

        // Assert
    }

    // Tests for the method 'removeMember'

    @Test
    void testRemoveMemberSuccessfully() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testRemoveMemberWithNonexistentUser() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    void testRemoveMemberWithUnauthorizedUser() {
        // Arrange

        // Act

        // Assert
    }
}