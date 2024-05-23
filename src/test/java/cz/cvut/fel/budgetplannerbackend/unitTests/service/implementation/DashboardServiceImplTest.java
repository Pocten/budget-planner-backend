package cz.cvut.fel.budgetplannerbackend.unitTests.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.entity.*;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.DashboardMapper;
import cz.cvut.fel.budgetplannerbackend.repository.*;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import cz.cvut.fel.budgetplannerbackend.service.DashboardAccessService;
import cz.cvut.fel.budgetplannerbackend.service.DashboardRoleService;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private DashboardMapper dashboardMapper;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    AccessLevelRepository accessLevelRepository;

    @Mock
    private CategoryPriorityRepository categoryPriorityRepository;

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private FinancialGoalRepository financialGoalRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DashboardAccessRepository dashboardAccessRepository;

    @Mock
    private DashboardRoleRepository dashboardRoleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DashboardRoleService dashboardRoleService;

    @Mock
    private DashboardAccessService dashboardAccessService;

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
        Mockito.reset(dashboardRepository, dashboardMapper, securityUtils, dashboardRoleService,
                dashboardAccessRepository, userRepository, accessLevelRepository, roleRepository);
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
        Long userId = 1L;
        Long dashboardId = 1L;
        User testUser = new User();
        testUser.setId(userId);
        Dashboard testDashboard = new Dashboard();
        testDashboard.setId(dashboardId);
        testDashboard.setUser(testUser);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(dashboardRepository.findByIdAndUserId(dashboardId, userId)).thenReturn(Optional.of(testDashboard));
        when(dashboardMapper.toDto(testDashboard)).thenReturn(new DashboardDto(dashboardId, "Title", "Description", LocalDateTime.now(), userId));

        // Act
        DashboardDto result = dashboardService.findUserDashboardById(userId, dashboardId);

        // Assert
        assertNotNull(result);
        assertEquals(dashboardId, result.id());
        verify(dashboardRepository, times(1)).findByIdAndUserId(dashboardId, userId);
    }

    @Test
    void testFindUserDashboardByIdWithUnauthorizedUser() {
        // Arrange
        Long dashboardId = 1L;
        Long userId = 2L;
        User testUser = new User();
        testUser.setId(userId);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(dashboardRepository.findByIdAndUserId(dashboardId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.findUserDashboardById(userId, dashboardId));
        verify(dashboardRepository, times(1)).findByIdAndUserId(dashboardId, userId);
    }

    @Test
    void testFindUserDashboardByIdWithNonexistentId() {
        // Arrange
        Long dashboardId = 1L;
        Long userId = 1L;
        User testUser = new User();
        testUser.setId(userId);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(dashboardRepository.findByIdAndUserId(dashboardId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.findUserDashboardById(userId, dashboardId));
        verify(dashboardRepository, times(1)).findByIdAndUserId(dashboardId, userId);
    }

    @Test
    void testFindDashboardByIdWithValidId() {
        // Arrange
        Long dashboardId = 1L;
        Dashboard testDashboard = new Dashboard();
        testDashboard.setId(dashboardId);

        when(dashboardRepository.findById(dashboardId)).thenReturn(Optional.of(testDashboard));
        when(dashboardMapper.toDto(testDashboard)).thenReturn(new DashboardDto(dashboardId, "Title", "Description", LocalDateTime.now(), 1L));

        // Act
        DashboardDto result = dashboardService.findDashboardById(dashboardId);

        // Assert
        assertNotNull(result);
        assertEquals(dashboardId, result.id());
        verify(dashboardRepository, times(1)).findById(dashboardId);
    }

    @Test
    void testFindDashboardByIdWithNonexistentId() {
        // Arrange
        Long dashboardId = 1L;

        when(dashboardRepository.findById(dashboardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.findDashboardById(dashboardId));
        verify(dashboardRepository, times(1)).findById(dashboardId);
    }

    @Test
    void testCreateDashboardSuccessfully() {
        // Arrange
        Long userId = 1L;
        User testUser = new User();
        testUser.setId(userId);

        Dashboard dashboard = new Dashboard();
        dashboard.setTitle("New Dashboard");

        DashboardDto dashboardDto = new DashboardDto(null, "New Dashboard", "Description", null, userId);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser)); // Добавьте этот мок
        when(dashboardMapper.toEntity(any(DashboardDto.class))).thenReturn(dashboard);
        when(dashboardRepository.save(any(Dashboard.class))).thenAnswer(invocation -> {
            Dashboard savedDashboard = invocation.getArgument(0);
            savedDashboard.setId(1L);
            return savedDashboard;
        });
        when(dashboardMapper.toDto(any(Dashboard.class))).thenReturn(new DashboardDto(1L, "New Dashboard", "Description", LocalDateTime.now(), userId));

        doNothing().when(dashboardRoleService).assignRoleToUserInDashboard(userId, 1L, ERole.NONE);
        doNothing().when(dashboardAccessService).grantAccess(userId, 1L, EAccessLevel.OWNER);

        // Act
        DashboardDto result = dashboardService.createDashboard(userId, dashboardDto);

        // Assert
        assertNotNull(result);
        assertEquals("New Dashboard", result.title());
        verify(dashboardRepository, times(1)).save(any(Dashboard.class));
        verify(dashboardRoleService, times(1)).assignRoleToUserInDashboard(userId, 1L, ERole.NONE);
        verify(dashboardAccessService, times(1)).grantAccess(userId, 1L, EAccessLevel.OWNER);
    }

    @Test
    void testCreateDashboardWithUnauthorizedUser() {
        // Arrange
        Long userId = 1L;
        User testUser = new User();
        testUser.setId(userId);

        DashboardDto dashboardDto = new DashboardDto(null, "New Dashboard", "Description", null, userId);

        when(securityUtils.getCurrentUser()).thenReturn(null);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.createDashboard(userId, dashboardDto));
        verify(dashboardRepository, never()).save(any(Dashboard.class));
    }

    @Test
    void testUpdateDashboardSuccessfully() {
        // Arrange
        Long userId = 1L;
        Long dashboardId = 1L;
        User testUser = new User();
        testUser.setId(userId);

        Dashboard dashboard = new Dashboard();
        dashboard.setId(dashboardId);
        dashboard.setUser(testUser);

        DashboardDto dashboardDto = new DashboardDto(dashboardId, "Updated Title", "Updated Description", LocalDateTime.now(), userId);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(dashboardRepository.findByIdAndUserId(dashboardId, userId)).thenReturn(Optional.of(dashboard));
        when(dashboardMapper.toEntity(dashboardDto)).thenReturn(dashboard);
        when(dashboardRepository.save(dashboard)).thenReturn(dashboard);
        when(dashboardMapper.toDto(dashboard)).thenReturn(dashboardDto);

        // Act
        DashboardDto result = dashboardService.updateDashboard(userId, dashboardId, dashboardDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.title());
        verify(dashboardRepository, times(1)).save(dashboard);
    }

    @Test
    void testUpdateDashboardWithNonexistentId() {
        // Arrange
        Long userId = 1L;
        Long dashboardId = 1L;
        User testUser = new User();
        testUser.setId(userId);

        DashboardDto dashboardDto = new DashboardDto(dashboardId, "Updated Title", "Updated Description", LocalDateTime.now(), userId);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(dashboardRepository.findByIdAndUserId(dashboardId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.updateDashboard(userId, dashboardId, dashboardDto));
        verify(dashboardRepository, never()).save(any(Dashboard.class));
    }

    @Test
    void testUpdateDashboardWithUnauthorizedAccess() {
        // Arrange
        Long userId = 1L;
        Long dashboardId = 1L;
        User testUser = new User();
        testUser.setId(userId);

        Dashboard dashboard = new Dashboard();
        dashboard.setId(dashboardId);
        dashboard.setUser(new User()); // different user

        DashboardDto dashboardDto = new DashboardDto(dashboardId, "Updated Title", "Updated Description", LocalDateTime.now(), userId);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(dashboardRepository.findByIdAndUserId(dashboardId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.updateDashboard(userId, dashboardId, dashboardDto));
        verify(dashboardRepository, never()).save(any(Dashboard.class));
    }


    // Tests for the method 'deleteDashboard'

    @Test
    void testDeleteDashboardSuccessfully() {
        // Arrange
        Long userId = 1L;
        Long dashboardId = 1L;
        User testUser = new User();
        testUser.setId(userId);
        Dashboard testDashboard = new Dashboard();
        testDashboard.setId(dashboardId);
        testDashboard.setUser(testUser);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(dashboardRepository.findByIdAndUserId(dashboardId, userId)).thenReturn(Optional.of(testDashboard));

        // Act
        dashboardService.deleteDashboard(userId, dashboardId);

        // Assert
        verify(dashboardRepository, times(1)).delete(testDashboard);
        verify(categoryPriorityRepository, times(1)).deleteByDashboardId(dashboardId);
        verify(financialRecordRepository, times(1)).deleteByDashboardId(dashboardId);
        verify(financialGoalRepository, times(1)).deleteByDashboardId(dashboardId);
        verify(budgetRepository, times(1)).deleteByDashboardId(dashboardId);
        verify(categoryRepository, times(1)).deleteByDashboardId(dashboardId);
        verify(dashboardAccessRepository, times(1)).deleteByDashboardId(dashboardId);
        verify(dashboardRoleRepository, times(1)).deleteByDashboardId(dashboardId);
    }


    @Test
    void testDeleteDashboardWithNonexistentId() {
        // Arrange
        Long userId = 1L;
        Long dashboardId = 999L;

        when(securityUtils.getCurrentUser()).thenReturn(new User());
        when(dashboardRepository.findByIdAndUserId(dashboardId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.deleteDashboard(userId, dashboardId));
    }


    @Test
    void testDeleteDashboardWithUnauthorizedUser() {
        // Arrange
        Long userId = 1L;
        Long dashboardId = 1L;
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);

        when(securityUtils.getCurrentUser()).thenReturn(unauthorizedUser);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.deleteDashboard(userId, dashboardId));
    }

    // Tests for the method 'addMember'

    @Test
    void testAddMemberSuccessfully() throws Exception {
        // Arrange
        Long dashboardId = 1L;
        String usernameOrEmail = "testUser2";
        Long userId = 1L;
        User testUser = new User();
        testUser.setId(userId);
        Dashboard testDashboard = new Dashboard();
        testDashboard.setId(dashboardId);
        User userToAdd = new User();
        userToAdd.setId(2L);
        userToAdd.setUserName(usernameOrEmail);

        AccessLevel viewerAccessLevel = new AccessLevel();
        viewerAccessLevel.setLevel(EAccessLevel.VIEWER);

        Role noneRole = new Role();
        noneRole.setName(ERole.NONE);

        DashboardAccess existingAccess = new DashboardAccess();
        existingAccess.setAccessLevel(viewerAccessLevel);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);
        when(dashboardRepository.findById(dashboardId)).thenReturn(Optional.of(testDashboard));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findUserByUserNameOrUserEmail(usernameOrEmail)).thenReturn(Optional.of(userToAdd));
        when(dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId)).thenReturn(Optional.of(existingAccess));
        when(accessLevelRepository.findByLevel(EAccessLevel.VIEWER)).thenReturn(Optional.of(viewerAccessLevel));
        when(roleRepository.findByName(ERole.NONE)).thenReturn(Optional.of(noneRole));

        // Act
        dashboardService.addMember(dashboardId, usernameOrEmail, userId);

        // Assert
        verify(dashboardAccessRepository, times(1)).save(any(DashboardAccess.class));
        verify(dashboardRoleService, times(1)).assignRoleToUserInDashboard(userToAdd.getId(), dashboardId, ERole.NONE);
    }

    @Test
    void testAddMemberWithNonexistentDashboard() {
        // Arrange
        Long dashboardId = 999L;
        String usernameOrEmail = "testUser2";
        Long userId = 1L;

        when(securityUtils.getCurrentUser()).thenReturn(new User());
        when(dashboardRepository.findById(dashboardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.addMember(dashboardId, usernameOrEmail, userId));
    }


    @Test
    void testAddMemberWithUnauthorizedUser() {
        // Arrange
        Long dashboardId = 1L;
        String usernameOrEmail = "testUser2";
        Long userId = 1L;
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);

        when(securityUtils.getCurrentUser()).thenReturn(unauthorizedUser);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> dashboardService.addMember(dashboardId, usernameOrEmail, userId));
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