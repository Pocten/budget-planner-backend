package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.BudgetDto;

import java.util.List;

public interface BudgetService {
    List<BudgetDto> findAllBudgetsByDashboardId(Long dashboardId);

    BudgetDto findBudgetByIdAndDashboardId(Long id, Long dashboardId);

    BudgetDto createBudget(Long dashboardId, BudgetDto budgetDto);

    BudgetDto updateBudget(Long dashboardId, Long budgetId, BudgetDto budgetDto);

    void deleteBudget(Long dashboardId, Long budgetId);
}
