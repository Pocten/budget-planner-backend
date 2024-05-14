package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;
import cz.cvut.fel.budgetplannerbackend.entity.Budget;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialGoal;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.FinancialGoalMapper;
import cz.cvut.fel.budgetplannerbackend.repository.BudgetRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.FinancialGoalRepository;
import cz.cvut.fel.budgetplannerbackend.service.FinancialGoalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialGoalServiceImpl implements FinancialGoalService {

    private final FinancialGoalRepository financialGoalRepository;
    private final DashboardRepository dashboardRepository;  // Changed from BudgetRepository
    private final FinancialGoalMapper financialGoalMapper;
    private static final Logger LOG = LoggerFactory.getLogger(FinancialGoalServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<FinancialGoalDto> findAllFinancialGoalsByDashboardId(Long dashboardId) {
        LOG.info("Fetching all financial goals for dashboard id: {}", dashboardId);
        List<FinancialGoal> financialGoals = financialGoalRepository.findByDashboardId(dashboardId);
        return financialGoals.stream()
                .map(financialGoalMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialGoalDto findFinancialGoalByIdAndDashboardId(Long dashboardId, Long goalId) {
        LOG.info("Fetching financial goal with id: {} for dashboard id: {}", goalId, dashboardId);
        FinancialGoal financialGoal = financialGoalRepository.findByIdAndDashboardId(goalId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialGoal not found with id: " + goalId + " for dashboard id: " + dashboardId));
        return financialGoalMapper.toDto(financialGoal);
    }

    @Override
    @Transactional
    public FinancialGoalDto createFinancialGoal(Long dashboardId, FinancialGoalDto financialGoalDto) {
        LOG.info("Creating new financial goal for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        FinancialGoal financialGoal = financialGoalMapper.toEntity(financialGoalDto);
        financialGoal.setDashboard(dashboard);
        FinancialGoal savedFinancialGoal = financialGoalRepository.save(financialGoal);
        return financialGoalMapper.toDto(savedFinancialGoal);
    }

    @Override
    @Transactional
    public FinancialGoalDto updateFinancialGoal(Long dashboardId, Long goalId, FinancialGoalDto financialGoalDto) {
        LOG.info("Updating financial goal with id: {} for dashboard id: {}", goalId, dashboardId);
        FinancialGoal financialGoal = financialGoalRepository.findByIdAndDashboardId(goalId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialGoal not found with id: " + goalId + " for dashboard id: " + dashboardId));

        if (financialGoalDto.title() != null) financialGoal.setTitle(financialGoalDto.title());
        if (financialGoalDto.targetAmount() != null) financialGoal.setTargetAmount(financialGoalDto.targetAmount());
        if (financialGoalDto.currentAmount() != null) financialGoal.setCurrentAmount(financialGoalDto.currentAmount());
        if (financialGoalDto.deadline() != null) financialGoal.setDeadline(financialGoalDto.deadline());

        FinancialGoal updatedFinancialGoal = financialGoalRepository.save(financialGoal);
        LOG.info("Updated financial goal with id: {} for dashboard id: {}", goalId, dashboardId);
        return financialGoalMapper.toDto(updatedFinancialGoal);
    }

    @Override
    @Transactional
    public void deleteFinancialGoal(Long dashboardId, Long goalId) {
        LOG.info("Deleting financial goal with id: {} for dashboard id: {}", goalId, dashboardId);
        FinancialGoal financialGoal = financialGoalRepository.findByIdAndDashboardId(goalId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialGoal not found with id: " + goalId + " for dashboard id: " + dashboardId));
        financialGoalRepository.delete(financialGoal);
    }
}
