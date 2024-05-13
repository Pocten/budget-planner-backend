package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;

import java.util.List;

public interface DashboardAccessService {

    void grantAccess(Long userId, Long dashboardId, EAccessLevel accessLevel);

    List<Long> getAccessibleDashboardIds(Long userId);

}