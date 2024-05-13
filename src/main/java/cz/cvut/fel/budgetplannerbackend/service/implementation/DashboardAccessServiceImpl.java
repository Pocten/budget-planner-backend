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

@Service
@RequiredArgsConstructor
public class DashboardAccessServiceImpl implements DashboardAccessService {

    private final DashboardAccessRepository dashboardAccessRepository;
    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;
    private final AccessLevelRepository accessLevelRepository;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardAccessServiceImpl.class);


    @Override
    @Transactional
    public void grantAccess(Long userId, Long dashboardId, EAccessLevel accessLevelEnum) {
        LOG.info("Granting access level {} to user {} on dashboard {}", accessLevelEnum, userId, dashboardId);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        AccessLevel accessLevel = accessLevelRepository.findByLevel(EAccessLevel.valueOf(accessLevelEnum.name())).orElseThrow(() -> new EntityNotFoundException("AccessLevel", accessLevelEnum.name()));

        Optional<DashboardAccess> existingAccess = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);
        existingAccess.ifPresentOrElse(dashboardAccess -> {
            dashboardAccess.setAccessLevel(accessLevel);
            dashboardAccessRepository.save(dashboardAccess);
            LOG.info("Updated access level for user {} on dashboard {}", userId, dashboardId);
        }, () -> {
            DashboardAccess newAccess = new DashboardAccess(null, user, dashboard, accessLevel);
            dashboardAccessRepository.save(newAccess);
            LOG.info("Granted new access for user {} on dashboard {}", userId, dashboardId);
        });
    }

    @Override
    public List<Long> getAccessibleDashboardIds(Long userId) {
        LOG.info("Retrieving accessible dashboards for user {}", userId);
        List<Long> dashboardIds = dashboardAccessRepository.findAllByUserId(userId)
                .stream()
                .map(access -> access.getDashboard().getId())
                .toList();
        LOG.info("Found {} accessible dashboards for user {}", dashboardIds.size(), userId);
        return dashboardIds;
    }
}


