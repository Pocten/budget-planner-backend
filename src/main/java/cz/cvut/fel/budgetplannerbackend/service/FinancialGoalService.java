package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;

import java.util.List;

public interface FinancialGoalService {

    List<FinancialGoalDto> findAllFinancialGoalsByDashboardId(Long dashboardId);

    FinancialGoalDto findFinancialGoalByIdAndDashboardId(Long dashboardId, Long goalId);

    FinancialGoalDto createFinancialGoal(Long dashboardId, FinancialGoalDto financialGoalDto);

    FinancialGoalDto updateFinancialGoal(Long dashboardId, Long goalId, FinancialGoalDto financialGoalDto);

    void deleteFinancialGoal(Long dashboardId, Long goalId);
}

