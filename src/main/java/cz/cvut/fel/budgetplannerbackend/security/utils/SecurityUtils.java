package cz.cvut.fel.budgetplannerbackend.security.utils;


import cz.cvut.fel.budgetplannerbackend.entity.DashboardAccess;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardAccessRepository;
import cz.cvut.fel.budgetplannerbackend.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final DashboardAccessRepository dashboardAccessRepository;

    private static final Logger LOG = LoggerFactory.getLogger(SecurityUtils.class);

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails && userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }
        throw new IllegalStateException("No user is currently authenticated");
    }

    public void checkAuthenticatedUser(Long userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            LOG.error("Security breach attempt: User with id {} tried to access resources for user with id {}", currentUser.getId(), userId);
            throw new AccessDeniedException("User with id " + currentUser.getId() + " is not authorized to perform this operation for user with id " + userId);
        }
        LOG.info("User with id {} authorized successfully for access to user with id {}", currentUser.getId(), userId);
    }

    public void checkAccessLevel(Long userId, Long dashboardId, EAccessLevel requiredLevel) {
        Optional<DashboardAccess> access = dashboardAccessRepository.findByUserIdAndDashboardId(userId, dashboardId);
        if (access.isEmpty() || access.get().getAccessLevel().getLevel().compareTo(requiredLevel) < 0) {
            LOG.error("User with id {} does not have required access level {} for dashboard with id {}", userId, requiredLevel, dashboardId);
            throw new AccessDeniedException("Insufficient permissions.");
        }
        LOG.info("Access level check passed for user id {} on dashboard id {}", userId, dashboardId);
    }
}