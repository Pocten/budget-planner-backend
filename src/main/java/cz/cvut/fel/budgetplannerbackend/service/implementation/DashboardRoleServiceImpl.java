package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardRoleDto;
import cz.cvut.fel.budgetplannerbackend.entity.*;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRoleRepository;
import cz.cvut.fel.budgetplannerbackend.repository.RoleRepository;
import cz.cvut.fel.budgetplannerbackend.repository.UserRepository;
import cz.cvut.fel.budgetplannerbackend.service.DashboardRoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardRoleServiceImpl implements DashboardRoleService {

    private final DashboardRoleRepository dashboardRoleRepository;
    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;
    private final RoleRepository roleRepository;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardRoleServiceImpl.class);

    @Override
    @Transactional
    public void assignRoleToUserInDashboard(Long userId, Long dashboardId, ERole roleName) {
        LOG.info("Assigning role {} to user {} on dashboard {}", roleName, userId, dashboardId);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        Dashboard dashboard = dashboardRepository.findById(dashboardId).orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        Role role = roleRepository.findByName(ERole.valueOf(roleName.name())).orElseThrow(() -> new EntityNotFoundException("Role", roleName.name()));

        Optional<DashboardRole> existingRole = dashboardRoleRepository.findByUserIdAndDashboardId(userId, dashboardId);
        existingRole.ifPresentOrElse(dashboardRole -> {
            dashboardRole.setRole(role);
            dashboardRoleRepository.save(dashboardRole);
            LOG.info("Updated existing role for user {} on dashboard {}", userId, dashboardId);
        }, () -> {
            DashboardRole newRole = new DashboardRole(null, user, dashboard, role);
            dashboardRoleRepository.save(newRole);
            LOG.info("Assigned new role for user {} on dashboard {}", userId, dashboardId);
        });
    }

}

