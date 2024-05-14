package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.dto.members.DashboardMemberDto;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;

import java.util.List;

public interface DashboardService {
    List<DashboardDto> findAllDashboardsByUserId(Long userId);

    DashboardDto findUserDashboardById(Long userId, Long dashboardId);

    DashboardDto findDashboardById(Long dashboardId);

    List<DashboardDto> findAccessibleDashboards();

    DashboardDto createDashboard(Long userId, DashboardDto dashboardDto);

    DashboardDto updateDashboard(Long userId, Long id, DashboardDto dashboardDto);

    void deleteDashboard(Long userId, Long id);

    void addMember(Long dashboardId, String usernameOrEmail, Long requesterUserId) throws Exception;

    List<DashboardMemberDto> findMembersByDashboardId(Long dashboardId);

    void changeAccessLevel(Long dashboardId, String usernameOrEmail, Long userId, EAccessLevel newAccessLevel) throws Exception;

    void removeMember(Long dashboardId, String usernameOrEmail, Long requesterUserId) throws Exception;

}
