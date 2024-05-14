package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.dto.members.DashboardMemberDto;
import cz.cvut.fel.budgetplannerbackend.entity.*;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.DashboardMapper;
import cz.cvut.fel.budgetplannerbackend.repository.*;
import cz.cvut.fel.budgetplannerbackend.service.DashboardAccessService;
import cz.cvut.fel.budgetplannerbackend.service.DashboardRoleService;
import cz.cvut.fel.budgetplannerbackend.service.DashboardService;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AccessLevelRepository accessLevelRepository;
    private final DashboardRepository dashboardRepository;
    private final DashboardAccessRepository dashboardAccessRepository;
    private final DashboardRoleRepository dashboardRoleRepository;
    private final DashboardAccessService dashboardAccessService;
    private final DashboardRoleService dashboardRoleService;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final FinancialGoalRepository financialGoalRepository;
    private final UserRepository userRepository;
    private final DashboardMapper dashboardMapper;
    private final SecurityUtils securityUtils;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);


    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto> findAllDashboardsByUserId(Long userId) {
        securityUtils.checkAuthenticatedUser(userId);
        LOG.info("Getting all dashboards for user id: {}", userId);
        List<Dashboard> dashboards = dashboardRepository.findAllByUserId(userId);
        return dashboards.stream()
                .map(dashboardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDto findUserDashboardById(Long userId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Getting dashboard with id: {} for user id: {}", dashboardId, userId);
        Dashboard dashboard = dashboardRepository.findByIdAndUserId(dashboardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        return dashboardMapper.toDto(dashboard);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDto findDashboardById(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Getting dashboard with id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        return dashboardMapper.toDto(dashboard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto> findAccessibleDashboards() {
        User currentUser = securityUtils.getCurrentUser();
        LOG.info("Fetching accessible dashboards for user id: {}", currentUser.getId());

        // We get the IDs of all dashboards to which the user has access
        List<Long> accessibleDashboardIds = dashboardAccessRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(access -> access.getDashboard().getId())
                .toList();

        if (accessibleDashboardIds.isEmpty()) {
            LOG.info("No accessible dashboards found for user id: {}", currentUser.getId());
            return List.of();
        }

        // Retrieving dashboards based on the received IDs
        List<Dashboard> accessibleDashboards = dashboardRepository.findAllById(accessibleDashboardIds);
        LOG.info("Found {} accessible dashboards for user id: {}", accessibleDashboards.size(), currentUser.getId());

        // Converting dashboards to DTOs
        return accessibleDashboards.stream()
                .map(dashboardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public DashboardDto createDashboard(Long userId, DashboardDto dashboardDto) {
        securityUtils.checkAuthenticatedUser(userId);
        LOG.info("Creating a new dashboard for user id: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        Dashboard dashboard = dashboardMapper.toEntity(dashboardDto);
        dashboard.setUser(user);
        Dashboard savedDashboard = dashboardRepository.save(dashboard);

        dashboardRoleService.assignRoleToUserInDashboard(userId, savedDashboard.getId(), ERole.NONE);
        dashboardAccessService.grantAccess(userId, savedDashboard.getId(), EAccessLevel.OWNER);

        return dashboardMapper.toDto(savedDashboard);
    }

    @Override
    @Transactional
    public DashboardDto updateDashboard(Long userId, Long dashboardId, DashboardDto dashboardDto) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Updating dashboard with id: {} for user id: {}", dashboardId, userId);
        Dashboard existingDashboard = dashboardRepository.findByIdAndUserId(dashboardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));

        if (dashboardDto.title() != null) {
            existingDashboard.setTitle(dashboardDto.title());
        }
        if (dashboardDto.description() != null) {
            existingDashboard.setDescription(dashboardDto.description());
        }

        Dashboard updatedDashboard = dashboardRepository.save(existingDashboard);
        LOG.info("Updated dashboard with id: {} for user id: {}", updatedDashboard.getId(), userId);
        return dashboardMapper.toDto(updatedDashboard);
    }

    @Override
    @Transactional
    public void deleteDashboard(Long userId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.OWNER);
        LOG.info("Initiating deletion of dashboard with id: {} for user id: {}", dashboardId, userId);
        Dashboard dashboard = dashboardRepository.findByIdAndUserId(dashboardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));

        LOG.info("Deleting all financial goals, budgets, categories, tags, and financial records associated with dashboard id: {}", dashboardId);
        dashboardAccessRepository.deleteByDashboardId(dashboardId);
        dashboardRoleRepository.deleteByDashboardId(dashboardId);
        financialGoalRepository.deleteByDashboardId(dashboardId);
        budgetRepository.deleteByDashboardId(dashboardId);
        categoryRepository.deleteByDashboardId(dashboardId);
        financialRecordRepository.deleteByDashboardId(dashboardId);

        LOG.info("Dashboard with id: {} successfully deleted, along with all its associated data.", dashboardId);
        dashboardRepository.delete(dashboard);
    }


    @Override
    @Transactional
    public void addMember(Long dashboardId, String usernameOrEmail, Long userId) throws Exception {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Attempting to add member to dashboard {}, initiated by user {}", dashboardId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with ID: " + dashboardId));

        User userToAdd = userRepository.findUserByUserNameOrUserEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username/email: " + usernameOrEmail));

        Optional<DashboardAccess> access = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);
        if (access.isPresent() && access.get().getAccessLevel().getLevel() == EAccessLevel.NONE) {
            LOG.error("Attempt by user {} to add a member to dashboard {} denied due to NONE access level.", userId, dashboardId);
            throw new AccessDeniedException("Users with 'NONE' access level cannot add members.");
        }

        AccessLevel viewerAccessLevel = accessLevelRepository.findByLevel(EAccessLevel.VIEWER)
                .orElseThrow(() -> new RuntimeException("Access level VIEWER not found"));

        DashboardAccess newAccess = new DashboardAccess(null, userToAdd, dashboard, viewerAccessLevel);
        dashboardAccessRepository.save(newAccess);
        LOG.info("User {} added to dashboard {} by {}", userToAdd.getUserName(), dashboard.getId(), user.getUserName());
    }

    @Override
    @Transactional
    public List<DashboardMemberDto> findMembersByDashboardId(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Starting to find members for dashboard {}", dashboardId);
        try {
            List<DashboardAccess> accesses = dashboardAccessRepository.findAllByDashboardId(dashboardId);
            LOG.debug("Access records retrieved for dashboard {}", dashboardId);

            List<DashboardRole> roles = dashboardRoleRepository.findAllByDashboardId(dashboardId);
            LOG.debug("Role records retrieved for dashboard {}", dashboardId);

            Map<Long, String> userIdToRole = roles.stream()
                    .collect(Collectors.toMap(
                            role -> role.getUser().getId(),
                            role -> role.getRole().getName().toString(),
                            (existing, replacement) -> existing));

            List<DashboardMemberDto> members = accesses.stream().map(access -> new DashboardMemberDto(
                    access.getUser().getId(),
                    access.getUser().getUserName(),
                    access.getUser().getUserEmail(),
                    access.getAccessLevel().getLevel().toString(),
                    userIdToRole.getOrDefault(access.getUser().getId(), "NONE")
            )).toList();
            LOG.info("Successfully found {} members for dashboard {}", members.size(), dashboardId);
            return members;
        } catch (Exception e) {
            LOG.error("Failed to find members for dashboard {}: {}", dashboardId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void changeAccessLevel(Long dashboardId, String usernameOrEmail, Long userId, EAccessLevel newAccessLevelEnum) throws Exception {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Attempting to change access level for a member on dashboard {}", dashboardId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with ID: " + dashboardId));

        User userToChangeAccess = userRepository.findUserByUserNameOrUserEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username/email: " + usernameOrEmail));

        Optional<DashboardAccess> access = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);
        if (access.isPresent()) {
            EAccessLevel currentAccessLevel = access.get().getAccessLevel().getLevel();
            if (currentAccessLevel == EAccessLevel.NONE || (currentAccessLevel == EAccessLevel.OWNER && userToChangeAccess.getId().equals(userId))) {
                LOG.error("User {} attempted to change own access level or change to OWNER on dashboard {}", userId, dashboardId);
                throw new AccessDeniedException("Insufficient permissions to change access levels.");
            }

            if (newAccessLevelEnum == EAccessLevel.OWNER) {
                LOG.error("Attempt to assign OWNER role to user {} on dashboard {}", userToChangeAccess.getId(), dashboardId);
                throw new AccessDeniedException("Cannot change access level to OWNER.");
            }

            AccessLevel newAccessLevel = accessLevelRepository.findByLevel(newAccessLevelEnum)
                    .orElseThrow(() -> new EntityNotFoundException("Access level not found: " + newAccessLevelEnum));

            DashboardAccess accessToChange = dashboardAccessRepository.findByUserIdAndDashboardId(userToChangeAccess.getId(), dashboardId)
                    .orElseThrow(() -> new EntityNotFoundException("No access found for user with ID: " + userToChangeAccess.getId() + " on dashboard with ID: " + dashboardId));
            accessToChange.setAccessLevel(newAccessLevel);
            dashboardAccessRepository.save(accessToChange);
            LOG.info("Access level for user {} changed to {} on dashboard {} by {}", userToChangeAccess.getUserName(), newAccessLevelEnum, dashboard.getId(), user.getUserName());
        } else {
            LOG.error("No existing access found for user {} on dashboard {}", usernameOrEmail, dashboardId);
            throw new AccessDeniedException("User not found on dashboard.");
        }
    }

    @Override
    @Transactional
    public void removeMember(Long dashboardId, String usernameOrEmail, Long userId) throws Exception {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Attempting to remove member from dashboard {}, initiated by user {}", dashboardId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with ID: " + dashboardId));

        User userToRemove = userRepository.findUserByUserNameOrUserEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username/email: " + usernameOrEmail));

        Optional<DashboardAccess> access = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);
        if (access.isPresent() && access.get().getAccessLevel().getLevel() == EAccessLevel.NONE) {
            LOG.error("User {} attempted to remove member with NONE access level on dashboard {}", userId, dashboardId);
            throw new AccessDeniedException("Users with 'NONE' access level cannot remove members.");
        }

        dashboardAccessRepository.findByUserIdAndDashboardId(userToRemove.getId(), dashboardId)
                .ifPresent(da -> {
                    dashboardAccessRepository.delete(da);
                    LOG.info("User {} removed from dashboard {} by {}", userToRemove.getUserName(), dashboard.getId(), user.getUserName());
                });
    }
}