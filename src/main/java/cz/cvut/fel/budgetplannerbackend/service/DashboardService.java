package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;

import java.util.List;

public interface DashboardService {
    List<DashboardDto> getAllDashboardsByUserId(Long userId);

    DashboardDto getUserDashboardById(Long userId, Long id);

    DashboardDto createDashboard(Long userId, DashboardDto dashboardDto);

    DashboardDto updateDashboard(Long userId, Long id, DashboardDto dashboardDto);

    void deleteDashboard(Long userId, Long id);
}
