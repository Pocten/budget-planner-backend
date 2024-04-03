package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;
import cz.cvut.fel.budgetplannerbackend.entity.Budget;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialGoal;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.FinancialGoalMapper;
import cz.cvut.fel.budgetplannerbackend.repository.BudgetRepository;
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
    private final BudgetRepository budgetRepository;
    private final FinancialGoalMapper financialGoalMapper;
    private static final Logger LOG = LoggerFactory.getLogger(FinancialGoalServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<FinancialGoalDto> findAllFinancialGoalsByBudgetId(Long dashboardId, Long budgetId) {
        LOG.info("Fetching all financial goals for dashboard id: {} and budget id: {}", dashboardId, budgetId);
        List<FinancialGoal> financialGoals = financialGoalRepository.findByBudgetId(budgetId);
        return financialGoals.stream()
                .map(financialGoalMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialGoalDto findFinancialGoalByIdAndBudgetId(Long dashboardId, Long budgetId, Long goalId) {
        LOG.info("Fetching financial goal with id: {} for dashboard id: {} and budget id: {}", goalId, dashboardId, budgetId);
        FinancialGoal financialGoal = financialGoalRepository.findByIdAndBudgetId(goalId, budgetId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialGoal not found with id: " + goalId + " for budget id: " + budgetId));
        return financialGoalMapper.toDto(financialGoal);
    }

    @Override
    @Transactional
    public FinancialGoalDto createFinancialGoal(Long dashboardId, Long budgetId, FinancialGoalDto financialGoalDto) {
        LOG.info("Creating new financial goal for dashboard id: {} and budget id: {}", dashboardId, budgetId);
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget", budgetId));
        FinancialGoal financialGoal = financialGoalMapper.toEntity(financialGoalDto);
        financialGoal.setBudget(budget);
        FinancialGoal savedFinancialGoal = financialGoalRepository.save(financialGoal);
        return financialGoalMapper.toDto(savedFinancialGoal);
    }

    @Override
    @Transactional
    public FinancialGoalDto updateFinancialGoal(Long dashboardId, Long budgetId, Long goalId, FinancialGoalDto financialGoalDto) {
        LOG.info("Updating financial goal with id: {} for dashboard id: {} and budget id: {}", goalId, dashboardId, budgetId);
        FinancialGoal financialGoal = financialGoalRepository.findByIdAndBudgetId(goalId, budgetId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialGoal not found with id: " + goalId + " for budget id: " + budgetId));

        // Update only non-null fields from financialGoalDto
        if (financialGoalDto.title() != null) financialGoal.setTitle(financialGoalDto.title());
        if (financialGoalDto.targetAmount() != null) financialGoal.setTargetAmount(financialGoalDto.targetAmount());
        if (financialGoalDto.currentAmount() != null) financialGoal.setCurrentAmount(financialGoalDto.currentAmount());
        if (financialGoalDto.deadline() != null) financialGoal.setDeadline(financialGoalDto.deadline());

        FinancialGoal updatedFinancialGoal = financialGoalRepository.save(financialGoal);
        LOG.info("Updated financial goal with id: {} for dashboard id: {} and budget id: {}", goalId, dashboardId, budgetId);
        return financialGoalMapper.toDto(updatedFinancialGoal);
    }

    @Override
    @Transactional
    public void deleteFinancialGoal(Long dashboardId, Long budgetId, Long goalId) {
        LOG.info("Deleting financial goal with id: {} for dashboard id: {} and budget id: {}", goalId, dashboardId, budgetId);
        FinancialGoal financialGoal = financialGoalRepository.findByIdAndBudgetId(goalId, budgetId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialGoal not found with id: " + goalId + " for budget id: " + budgetId));
        financialGoalRepository.delete(financialGoal);
    }
}


