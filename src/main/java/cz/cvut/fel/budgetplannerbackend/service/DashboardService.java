package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;

import java.util.List;

public interface DashboardService {
    List<DashboardDto> findAllDashboardsByUserId(Long userId);

    DashboardDto findUserDashboardById(Long userId, Long id);

    DashboardDto createDashboard(Long userId, DashboardDto dashboardDto);

    DashboardDto updateDashboard(Long userId, Long id, DashboardDto dashboardDto);

    void deleteDashboard(Long userId, Long id);
}
