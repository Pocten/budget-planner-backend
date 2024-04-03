package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;

import java.util.List;

public interface FinancialGoalService {
    List<FinancialGoalDto> findAllFinancialGoalsByBudgetId(Long dashboardId, Long budgetId);
    FinancialGoalDto findFinancialGoalByIdAndBudgetId(Long dashboardId, Long budgetId, Long goalId);
    FinancialGoalDto createFinancialGoal(Long dashboardId, Long budgetId, FinancialGoalDto financialGoalDto);
    FinancialGoalDto updateFinancialGoal(Long dashboardId, Long budgetId, Long goalId, FinancialGoalDto financialGoalDto);
    void deleteFinancialGoal(Long dashboardId, Long budgetId, Long goalId);
}
