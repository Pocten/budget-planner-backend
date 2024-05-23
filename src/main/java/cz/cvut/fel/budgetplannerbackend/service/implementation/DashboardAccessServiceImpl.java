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
import cz.cvut.fel.budgetplannerbackend.service.DashboardAccessService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing access rights to dashboards for different users.
 */
@Service
@RequiredArgsConstructor
public class DashboardAccessServiceImpl implements DashboardAccessService {

    private final DashboardAccessRepository dashboardAccessRepository;
    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;
    private final AccessLevelRepository accessLevelRepository;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardAccessServiceImpl.class);

    /**
     * Grants or updates access to a dashboard for a specific user.
     * If the user already has access, their access level is updated.
     * Otherwise, a new access entry is created for the user.
     *
     * @param userId          The ID of the user.
     * @param dashboardId     The ID of the dashboard.
     * @param accessLevelEnum The access level to grant.
     * @throws EntityNotFoundException If the user, dashboard, or access level is not found.
     */
    @Override
    @Transactional
    public void grantAccess(Long userId, Long dashboardId, EAccessLevel accessLevelEnum) {
        LOG.info("Granting access level {} to user {} on dashboard {}", accessLevelEnum, userId, dashboardId);

        // Retrieve the user, dashboard, and access level entities from the database.
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        AccessLevel accessLevel = accessLevelRepository.findByLevel(EAccessLevel.valueOf(accessLevelEnum.name())).orElseThrow(() -> new EntityNotFoundException("AccessLevel", accessLevelEnum.name()));

        // Check if an access entry already exists for this user and dashboard.
        Optional<DashboardAccess> existingAccess = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);

        // Update existing access or create new access based on existence.
        existingAccess.ifPresentOrElse(
                dashboardAccess -> { // Update existing access level
                    dashboardAccess.setAccessLevel(accessLevel);
                    dashboardAccessRepository.save(dashboardAccess);
                    LOG.info("Updated access level for user {} on dashboard {}", userId, dashboardId);
                },
                () -> { // Grant new access
                    DashboardAccess newAccess = new DashboardAccess(null, user, dashboard, accessLevel);
                    dashboardAccessRepository.save(newAccess);
                    LOG.info("Granted new access for user {} on dashboard {}", userId, dashboardId);
                }
        );
    }

    /**
     * Retrieves a list of dashboard IDs that are accessible to a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of dashboard IDs.
     */
    @Override
    public List<Long> getAccessibleDashboardIds(Long userId) {
        LOG.info("Retrieving accessible dashboards for user {}", userId);
        // Retrieve the dashboard IDs from all access entries associated with the user.
        List<Long> dashboardIds = dashboardAccessRepository.findAllByUserId(userId)
                .stream()
                .map(access -> access.getDashboard().getId()) // Extract the dashboard ID from each access entry.
                .toList(); // Collect the IDs into a list.
        LOG.info("Found {} accessible dashboards for user {}", dashboardIds.size(), userId);
        return dashboardIds;
    }
}