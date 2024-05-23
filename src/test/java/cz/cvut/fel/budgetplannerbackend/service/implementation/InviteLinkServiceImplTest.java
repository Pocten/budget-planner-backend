package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.InviteLinkDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.InviteLink;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.InviteLinkMapper;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.InviteLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteLinkServiceImplTest {

    @Mock
    private InviteLinkRepository inviteLinkRepository;

    @Mock
    private InviteLinkMapper inviteLinkMapper;

    @Mock
    private DashboardAccessServiceImpl dashboardAccessService;

    @Mock
    private DashboardRoleServiceImpl dashboardRoleService;

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private InviteLinkServiceImpl inviteLinkService;

    private InviteLinkDto testInviteLinkDto;
    private InviteLink testInviteLink;
    private Dashboard testDashboard;

    @BeforeEach
    void setUp() {
        testDashboard = new Dashboard();
        testDashboard.setId(1L);

        testInviteLinkDto = new InviteLinkDto(1L, UUID.randomUUID().toString(), LocalDateTime.now().plusDays(30), true, 1L);
        testInviteLink = new InviteLink();
        testInviteLink.setLink(testInviteLinkDto.link());
        testInviteLink.setDashboard(testDashboard);
        testInviteLink.setExpiryDate(testInviteLinkDto.expiryDate());
        testInviteLink.setActive(testInviteLinkDto.active());
    }

    @Test
    void testCreateInviteLink_NewLink() {
        // Arrange
        InviteLink existingInviteLink = new InviteLink();
        existingInviteLink.setId(1L);
        existingInviteLink.setLink(UUID.randomUUID().toString());
        existingInviteLink.setDashboard(testDashboard);
        existingInviteLink.setExpiryDate(LocalDateTime.now().plusDays(30));
        existingInviteLink.setActive(true);

        when(inviteLinkRepository.findByDashboardIdAndIsActiveTrue(testInviteLinkDto.dashboardId())).thenReturn(Optional.of(existingInviteLink));
        when(dashboardRepository.findById(testInviteLinkDto.dashboardId())).thenReturn(Optional.of(testDashboard));
        when(inviteLinkRepository.save(any(InviteLink.class))).thenReturn(testInviteLink);
        when(inviteLinkMapper.toDto(any(InviteLink.class))).thenReturn(testInviteLinkDto);

        // Act
        InviteLinkDto result = inviteLinkService.createInviteLink(testInviteLinkDto);

        // Assert
        assertNotNull(result);
        assertEquals(testInviteLinkDto.dashboardId(), result.dashboardId());
        verify(inviteLinkRepository, times(1)).delete(existingInviteLink);
        verify(inviteLinkRepository, times(1)).save(any(InviteLink.class));
    }


    @Test
    void testCheckAndRefreshLinks() {
        // Arrange
        when(inviteLinkRepository.findExpiredLinks(any(LocalDateTime.class))).thenReturn(List.of(testInviteLink));

        // Act
        inviteLinkService.checkAndRefreshLinks();

        // Assert
        verify(inviteLinkRepository, times(1)).findExpiredLinks(any(LocalDateTime.class));
        verify(inviteLinkRepository, times(1)).save(any(InviteLink.class));
    }

    @Test
    void testActivateLink() {
        // Arrange
        when(inviteLinkRepository.findByLink(testInviteLink.getLink())).thenReturn(Optional.of(testInviteLink));

        // Act
        inviteLinkService.activateLink(testInviteLink.getLink());

        // Assert
        verify(inviteLinkRepository, times(1)).findByLink(testInviteLink.getLink());
        verify(inviteLinkRepository, times(1)).save(testInviteLink);
        assertTrue(testInviteLink.isActive());
    }

    @Test
    void testDeactivateLink() {
        // Arrange
        when(inviteLinkRepository.findByLink(testInviteLink.getLink())).thenReturn(Optional.of(testInviteLink));

        // Act
        inviteLinkService.deactivateLink(testInviteLink.getLink());

        // Assert
        verify(inviteLinkRepository, times(1)).findByLink(testInviteLink.getLink());
        verify(inviteLinkRepository, times(1)).save(testInviteLink);
        assertFalse(testInviteLink.isActive());
    }

    @Test
    void testUseInviteLink_Success() {
        // Arrange
        Long userId = 1L;
        when(inviteLinkRepository.findByLink(testInviteLink.getLink())).thenReturn(Optional.of(testInviteLink));

        // Act
        boolean result = inviteLinkService.useInviteLink(testInviteLink.getLink(), userId);

        // Assert
        assertTrue(result);
        verify(dashboardAccessService, times(1)).grantAccess(userId, testDashboard.getId(), EAccessLevel.VIEWER);
        verify(dashboardRoleService, times(1)).assignRoleToUserInDashboard(userId, testDashboard.getId(), ERole.NONE);
    }

    @Test
    void testUseInviteLink_LinkExpired() {
        // Arrange
        Long userId = 1L;
        testInviteLink.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(inviteLinkRepository.findByLink(testInviteLink.getLink())).thenReturn(Optional.of(testInviteLink));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> inviteLinkService.useInviteLink(testInviteLink.getLink(), userId));
        verify(dashboardAccessService, never()).grantAccess(userId, testDashboard.getId(), EAccessLevel.VIEWER);
        verify(dashboardRoleService, never()).assignRoleToUserInDashboard(userId, testDashboard.getId(), ERole.NONE);
    }

    @Test
    void testUseInviteLink_LinkNotFound() {
        // Arrange
        Long userId = 1L;
        when(inviteLinkRepository.findByLink(testInviteLink.getLink())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> inviteLinkService.useInviteLink(testInviteLink.getLink(), userId));
        verify(dashboardAccessService, never()).grantAccess(userId, testDashboard.getId(), EAccessLevel.VIEWER);
        verify(dashboardRoleService, never()).assignRoleToUserInDashboard(userId, testDashboard.getId(), ERole.NONE);
    }
}

