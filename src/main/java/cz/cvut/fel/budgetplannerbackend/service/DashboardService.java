package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;

import java.util.List;

public interface DashboardService {

    List<DashboardDto> getAllDashboardsByUserId(Long userId);

    DashboardDto getDashboardById(Long id);

    DashboardDto createDashboard(DashboardDto dashboardDto);

    DashboardDto updateDashboard(Long id, DashboardDto dashboardDto);

    void deleteDashboard(Long id);

}
