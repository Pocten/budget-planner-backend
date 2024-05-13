package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;

public interface DashboardRoleService {

    void assignRoleToUserInDashboard(Long userId, Long dashboardId, ERole roleName);

}
