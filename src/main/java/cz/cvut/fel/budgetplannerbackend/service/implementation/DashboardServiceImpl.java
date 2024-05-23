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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing dashboards.
 */
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
    private final CategoryPriorityRepository categoryPriorityRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final FinancialGoalRepository financialGoalRepository;
    private final UserRepository userRepository;
    private final DashboardMapper dashboardMapper;
    private final SecurityUtils securityUtils;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);

    /**
     * Retrieves all dashboards that a user has access to.
     *
     * @param userId The ID of the user.
     * @return A list of dashboard DTOs.
     * @throws AccessDeniedException If the authenticated user is not authorized to access the given userId.
     */
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

    /**
     * Retrieves a specific dashboard by its ID, but only if the given user has access.
     *
     * @param userId      The ID of the user.
     * @param dashboardId The ID of the dashboard.
     * @return The dashboard DTO.
     * @throws EntityNotFoundException If the dashboard is not found or the user doesn't have access.
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardDto findUserDashboardById(Long userId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Getting dashboard with id: {} for user id: {}", dashboardId, userId);
        Dashboard dashboard = dashboardRepository.findByIdAndUserId(dashboardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        return dashboardMapper.toDto(dashboard);
    }

    /**
     * Retrieves a specific dashboard by its ID.
     *
     * @param dashboardId The ID of the dashboard.
     * @return The dashboard DTO.
     * @throws EntityNotFoundException If the dashboard is not found.
     * @throws AccessDeniedException If the authenticated user does not have at least VIEWER access.
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardDto findDashboardById(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Getting dashboard with id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        return dashboardMapper.toDto(dashboard);
    }

    /**
     * Retrieves a list of dashboards that the currently authenticated user has access to,
     * excluding dashboards created by the user themselves.
     *
     * @return A list of dashboard DTOs representing accessible dashboards.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DashboardDto> findAccessibleDashboards() {
        User currentUser = securityUtils.getCurrentUser();
        LOG.info("Fetching accessible dashboards for user id: {}", currentUser.getId());

        // Get the IDs of all dashboards to which the user has access and which he did not create
        List<Long> accessibleDashboardIds = dashboardAccessRepository.findAllByUserId(currentUser.getId())
                .stream()
                .filter(access -> !access.getDashboard().getUser().getId().equals(currentUser.getId())) // Exclude dashboards where the user is the creator
                .map(access -> access.getDashboard().getId())
                .distinct() // Remove possible duplicates
                .toList();

        if (accessibleDashboardIds.isEmpty()) {
            LOG.info("No accessible dashboards found for user id: {}", currentUser.getId());
            return List.of();
        }

        // Get dashboards based on the found IDs
        List<Dashboard> accessibleDashboards = dashboardRepository.findAllById(accessibleDashboardIds);
        LOG.info("Found {} accessible dashboards for user id: {}", accessibleDashboards.size(), currentUser.getId());

        // Convert dashboards to DTO
        return accessibleDashboards.stream()
                .map(dashboardMapper::toDto)
                .toList();
    }


    /**
     * Creates a new dashboard.
     *
     * @param userId       The ID of the user creating the dashboard.
     * @param dashboardDto The dashboard DTO object containing the dashboard data.
     * @return The created dashboard DTO.
     * @throws EntityNotFoundException If the user is not found.
     * @throws AccessDeniedException If the authenticated user is not authorized to access the given userId.
     */
    @Override
    @Transactional
    public DashboardDto createDashboard(Long userId, DashboardDto dashboardDto) {
        securityUtils.checkAuthenticatedUser(userId);
        LOG.info("Creating a new dashboard for user id: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        Dashboard dashboard = dashboardMapper.toEntity(dashboardDto);
        dashboard.setUser(user);
        Dashboard savedDashboard = dashboardRepository.save(dashboard);

        // After creating the dashboard, assign the creator as OWNER and role NONE
        dashboardRoleService.assignRoleToUserInDashboard(userId, savedDashboard.getId(), ERole.NONE);
        dashboardAccessService.grantAccess(userId, savedDashboard.getId(), EAccessLevel.OWNER);

        return dashboardMapper.toDto(savedDashboard);
    }

    /**
     * Updates an existing dashboard.
     *
     * @param userId       The ID of the user updating the dashboard.
     * @param dashboardId  The ID of the dashboard to update.
     * @param dashboardDto The dashboard DTO object containing the updated dashboard data.
     * @return The updated dashboard DTO.
     * @throws EntityNotFoundException If the dashboard is not found.
     * @throws AccessDeniedException If the authenticated user does not have EDITOR rights to the dashboard.
     */
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

    /**
     * Deletes a dashboard and all associated data.
     *
     * @param userId      The ID of the user deleting the dashboard.
     * @param dashboardId The ID of the dashboard to delete.
     * @throws EntityNotFoundException If the dashboard is not found.
     * @throws AccessDeniedException If the authenticated user does not have OWNER rights to the dashboard.
     */
    @Override
    @Transactional
    public void deleteDashboard(Long userId, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.OWNER);
        LOG.info("Initiating deletion of dashboard with id: {} for user id: {}", dashboardId, userId);
        Dashboard dashboard = dashboardRepository.findByIdAndUserId(dashboardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));

        // Delete category priorities associated with the dashboard
        LOG.info("Deleting category priorities associated with dashboard id: {}", dashboardId);
        categoryPriorityRepository.deleteByDashboardId(dashboardId);

        // Delete financial records that may reference categories or budgets
        LOG.info("Deleting financial records associated with dashboard id: {}", dashboardId);
        financialRecordRepository.deleteByDashboardId(dashboardId);

        // Delete related data after deleting financial records
        LOG.info("Deleting financial goals, budgets and categories associated with dashboard id: {}", dashboardId);
        financialGoalRepository.deleteByDashboardId(dashboardId);
        budgetRepository.deleteByDashboardId(dashboardId);
        categoryRepository.deleteByDashboardId(dashboardId);

        // Delete dashboard accesses and roles
        LOG.info("Deleting access and roles associated with dashboard id: {}", dashboardId);
        dashboardAccessRepository.deleteByDashboardId(dashboardId);
        dashboardRoleRepository.deleteByDashboardId(dashboardId);

        // Finally, delete the dashboard itself
        dashboardRepository.delete(dashboard);
        LOG.info("Dashboard with id: {} successfully deleted, along with all its associated data.", dashboardId);
    }

    /**
     * Adds a member to a dashboard with the default access level VIEWER and role NONE.
     *
     * @param dashboardId      The ID of the dashboard.
     * @param usernameOrEmail The username or email of the user to add.
     * @param userId           The ID of the user initiating the add operation.
     * @throws Exception If the user does not exist, the dashboard does not exist,
     *                   or the current user has insufficient permissions.
     */
    @Override
    @Transactional
    public void addMember(Long dashboardId, String usernameOrEmail, Long userId) throws Exception {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Attempting to add member to dashboard {}, initiated by user {}", dashboardId, userId);

        // Retrieve the user who is initiating the add operation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Retrieve the dashboard to which the member is being added
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with ID: " + dashboardId));

        // Retrieve the user to be added as a member
        User userToAdd = userRepository.findUserByUserNameOrUserEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username/email: " + usernameOrEmail));

        // Check if the user initiating the add operation has at least VIEWER access
        Optional<DashboardAccess> access = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);
        if (access.isPresent() && access.get().getAccessLevel().getLevel() == EAccessLevel.NONE) {
            LOG.error("Attempt by user {} to add a member to dashboard {} denied due to NONE access level.", userId, dashboardId);
            throw new AccessDeniedException("Users with 'NONE' access level cannot add members.");
        }

        // Retrieve the VIEWER access level entity
        AccessLevel viewerAccessLevel = accessLevelRepository.findByLevel(EAccessLevel.VIEWER)
                .orElseThrow(() -> new RuntimeException("Access level VIEWER not found"));

        // Create a new DashboardAccess object to represent the member's access
        DashboardAccess newAccess = new DashboardAccess(null, userToAdd, dashboard, viewerAccessLevel);
        dashboardAccessRepository.save(newAccess);
        LOG.info("User {} added to dashboard {} by {}", userToAdd.getUserName(), dashboard.getId(), user.getUserName());

        // Assign role NONE to the new user on this dashboard
        dashboardRoleService.assignRoleToUserInDashboard(userToAdd.getId(), dashboardId, ERole.NONE);
        LOG.info("Role NONE assigned to user {} on dashboard {}", userToAdd.getUserName(), dashboardId);
    }

    /**
     * Retrieves a list of members for a given dashboard with their access levels and roles.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A list of DashboardMemberDto objects, each representing a member with their details.
     * @throws AccessDeniedException If the authenticated user does not have at least VIEWER access.
     */
    @Override
    @Transactional
    public List<DashboardMemberDto> findMembersByDashboardId(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Starting to find members for dashboard {}", dashboardId);
        try {
            // Get access information for all users on the specified dashboard
            List<DashboardAccess> accesses = dashboardAccessRepository.findAllByDashboardId(dashboardId);
            LOG.debug("Access records retrieved for dashboard {}", dashboardId);

            // Get role information for all users on the specified dashboard
            List<DashboardRole> roles = dashboardRoleRepository.findAllByDashboardId(dashboardId);
            LOG.debug("Role records retrieved for dashboard {}", dashboardId);

            // Create a map to easily access a user's role by their ID
            Map<Long, String> userIdToRole = roles.stream()
                    .collect(Collectors.toMap(
                            role -> role.getUser().getId(), // Key: user ID
                            role -> role.getRole().getName().toString(), // Value: role name
                            (existing, replacement) -> existing)); // Merge function for duplicate keys (keep existing)

            // Create a list of DashboardMemberDto objects
            List<DashboardMemberDto> members = accesses.stream().map(access -> new DashboardMemberDto(
                    access.getUser().getId(),
                    access.getUser().getUserName(),
                    access.getUser().getUserEmail(),
                    access.getAccessLevel().getLevel().toString(), // Get access level as String
                    userIdToRole.getOrDefault(access.getUser().getId(), "NONE") // Get role from map, default to "NONE"
            )).toList();

            LOG.info("Successfully found {} members for dashboard {}", members.size(), dashboardId);
            return members;

        } catch (Exception e) {
            LOG.error("Failed to find members for dashboard {}: {}", dashboardId, e.getMessage(), e);
            throw e; // Re-throw the exception to be handled at a higher level
        }
    }

    /**
     * Changes the access level of a member on a dashboard.
     *
     * @param dashboardId        The ID of the dashboard.
     * @param usernameOrEmail     The username or email of the member.
     * @param userId             The ID of the user initiating the change.
     * @param newAccessLevelEnum The new access level for the member.
     * @throws Exception If the user or dashboard does not exist, the new access level is invalid,
     *                   or the user initiating the change has insufficient permissions.
     */
    @Override
    @Transactional
    public void changeAccessLevel(Long dashboardId, String usernameOrEmail, Long userId, EAccessLevel newAccessLevelEnum) throws Exception {
        // Check if the user who is trying to change access level has EDITOR access
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Attempting to change access level for a member on dashboard {}", dashboardId);

        // Retrieve the user initiating the change
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Retrieve the dashboard
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with ID: " + dashboardId));

        // Retrieve the user whose access level needs to be changed
        User userToChangeAccess = userRepository.findUserByUserNameOrUserEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username/email: " + usernameOrEmail));

        // Check if the target user is the owner of the dashboard, if so throw an error
        if (dashboard.getUser().getId().equals(userToChangeAccess.getId())) {
            LOG.error("User {} attempted to change access level of the dashboard owner on dashboard {}", userId, dashboardId);
            throw new AccessDeniedException("Cannot change access level of the dashboard owner.");
        }

        // Check if the user initiating the change has necessary access level
        Optional<DashboardAccess> access = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);
        if (access.isPresent()) {
            EAccessLevel currentAccessLevel = access.get().getAccessLevel().getLevel();
            if (currentAccessLevel == EAccessLevel.NONE) {
                LOG.error("User {} with NONE access level attempted to change access level on dashboard {}", userId, dashboardId);
                throw new AccessDeniedException("Insufficient permissions to change access levels.");
            }

            // Trying to change to OWNER is not allowed, throw an error
            if (newAccessLevelEnum == EAccessLevel.OWNER) {
                LOG.error("Attempt to assign OWNER role to user {} on dashboard {}", userToChangeAccess.getId(), dashboardId);
                throw new AccessDeniedException("Cannot change access level to OWNER.");
            }

            // Get the AccessLevel entity for the new access level
            AccessLevel newAccessLevel = accessLevelRepository.findByLevel(newAccessLevelEnum)
                    .orElseThrow(() -> new EntityNotFoundException("Access level not found: " + newAccessLevelEnum));

            // Retrieve the DashboardAccess object that needs to be modified
            DashboardAccess accessToChange = dashboardAccessRepository.findByUserIdAndDashboardId(userToChangeAccess.getId(), dashboardId)
                    .orElseThrow(() -> new EntityNotFoundException("No access found for user with ID: " + userToChangeAccess.getId() + " on dashboard with ID: " + dashboardId));

            // Update the access level and save the changes
            accessToChange.setAccessLevel(newAccessLevel);
            dashboardAccessRepository.save(accessToChange);
            LOG.info("Access level for user {} changed to {} on dashboard {} by {}", userToChangeAccess.getUserName(), newAccessLevelEnum, dashboard.getId(), user.getUserName());
        } else {
            // If no existing access is found for the user, throw an error
            LOG.error("No existing access found for user {} on dashboard {}", usernameOrEmail, dashboardId);
            throw new AccessDeniedException("User not found on dashboard.");
        }
    }

    /**
     * Removes a member from a dashboard.
     *
     * @param dashboardId      The ID of the dashboard.
     * @param usernameOrEmail The username or email of the member to remove.
     * @param userId           The ID of the user initiating the remove operation.
     * @throws Exception If the user or dashboard does not exist, or the user initiating the remove operation
     *                   has insufficient permissions.
     */
    @Override
    @Transactional
    public void removeMember(Long dashboardId, String usernameOrEmail, Long userId) throws Exception {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Attempting to remove member from dashboard {}, initiated by user {}", dashboardId, userId);

        // Retrieve entities for the user initiating the removal, the dashboard, and the user to be removed
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard not found with ID: " + dashboardId));
        User userToRemove = userRepository.findUserByUserNameOrUserEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username/email: " + usernameOrEmail));

        // Check if the user being removed is the owner of the dashboard
        Optional<DashboardAccess> userToRemoveAccess = dashboardAccessRepository.findByUserIdAndDashboardId(userToRemove.getId(), dashboardId);
        if (userToRemoveAccess.isPresent() && userToRemoveAccess.get().getAccessLevel().getLevel() == EAccessLevel.OWNER) {
            // If the user trying to remove themselves is the owner, throw an error
            if (userToRemove.getId().equals(userId)) {
                LOG.error("User {} attempted to remove self as OWNER from dashboard {}", userId, dashboardId);
                throw new AccessDeniedException("The owner of the dashboard cannot remove themselves.");
            }
            // If someone else is trying to remove the owner, throw an error
            LOG.error("User {} attempted to remove the owner of dashboard {}", userId, dashboardId);
            throw new AccessDeniedException("The owner of the dashboard cannot be removed.");
        }

        // Allow users to remove themselves from the dashboard (if they are not the owner)
        if (userToRemove.getId().equals(userId)) {
            dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId)
                    .ifPresent(da -> {
                        dashboardAccessRepository.delete(da);
                        LOG.info("User {} removed themselves from dashboard {}", userId, dashboardId);
                    });
            return; // Stop further processing after removing the user
        }

        // Check if the user attempting to remove others has NONE access level
        Optional<DashboardAccess> access = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);
        if (access.isPresent() && access.get().getAccessLevel().getLevel() == EAccessLevel.NONE) {
            LOG.error("User {} attempted to remove member with NONE access level on dashboard {}", userId, dashboardId);
            throw new AccessDeniedException("Users with 'NONE' access level cannot remove members.");
        }

        // Remove the user from the dashboard
        dashboardAccessRepository.findByUserIdAndDashboardId(userToRemove.getId(), dashboardId)
                .ifPresent(da -> {
                    dashboardAccessRepository.delete(da);
                    LOG.info("User {} removed from dashboard {} by {}", userToRemove.getUserName(), dashboard.getId(), user.getUserName());
                });
    }
}