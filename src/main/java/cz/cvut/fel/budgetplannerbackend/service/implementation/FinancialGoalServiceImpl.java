package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialGoal;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.FinancialGoalMapper;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.FinancialGoalRepository;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import cz.cvut.fel.budgetplannerbackend.service.FinancialGoalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * Service class for managing financial goals.
 */
@Service
@RequiredArgsConstructor
public class FinancialGoalServiceImpl implements FinancialGoalService {

    private final FinancialGoalRepository financialGoalRepository;
    private final DashboardRepository dashboardRepository;
    private final FinancialGoalMapper financialGoalMapper;
    private final SecurityUtils securityUtils;

    private static final Logger LOG = LoggerFactory.getLogger(FinancialGoalServiceImpl.class);

    /**
     * Retrieves all financial goals associated with a specific dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A list of financial goal DTOs.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialGoalDto> findAllFinancialGoalsByDashboardId(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching all financial goals for dashboard id: {}", dashboardId);
        List<FinancialGoal> financialGoals = financialGoalRepository.findByDashboardId(dashboardId);
        return financialGoals.stream()
                .map(financialGoalMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a specific financial goal by its ID and dashboard ID.
     *
     * @param dashboardId The ID of the dashboard.
     * @param goalId      The ID of the financial goal.
     * @return The financial goal DTO.
     * @throws EntityNotFoundException If the financial goal is not found.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional(readOnly = true)
    public FinancialGoalDto findFinancialGoalByIdAndDashboardId(Long dashboardId, Long goalId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching financial goal with id: {} for dashboard id: {}", goalId, dashboardId);
        FinancialGoal financialGoal = financialGoalRepository.findByIdAndDashboardId(goalId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialGoal not found with id: " + goalId + " for dashboard id: " + dashboardId));
        return financialGoalMapper.toDto(financialGoal);
    }

    /**
     * Creates a new financial goal.
     *
     * @param dashboardId      The ID of the dashboard to associate the financial goal with.
     * @param financialGoalDto The financial goal DTO containing the data for the new goal.
     * @return The created financial goal DTO.
     * @throws EntityNotFoundException If the dashboard is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @Override
    @Transactional
    public FinancialGoalDto createFinancialGoal(Long dashboardId, FinancialGoalDto financialGoalDto) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Creating new financial goal for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        FinancialGoal financialGoal = financialGoalMapper.toEntity(financialGoalDto);
        financialGoal.setDashboard(dashboard);
        FinancialGoal savedFinancialGoal = financialGoalRepository.save(financialGoal);
        return financialGoalMapper.toDto(savedFinancialGoal);
    }

    /**
     * Updates an existing financial goal.
     *
     * @param dashboardId      The ID of the dashboard associated with the financial goal.
     * @param goalId           The ID of the financial goal to update.
     * @param financialGoalDto The financial goal DTO containing the updated data.
     * @return The updated financial goal DTO.
     * @throws EntityNotFoundException If the financial goal is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @Override
    @Transactional
    public FinancialGoalDto updateFinancialGoal(Long dashboardId, Long goalId, FinancialGoalDto financialGoalDto) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
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

    /**
     * Deletes a financial goal.
     *
     * @param dashboardId The ID of the dashboard associated with the financial goal.
     * @param goalId      The ID of the financial goal to delete.
     * @throws EntityNotFoundException If the financial goal is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @Override
    @Transactional
    public void deleteFinancialGoal(Long dashboardId, Long goalId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Deleting financial goal with id: {} for dashboard id: {}", goalId, dashboardId);
        FinancialGoal financialGoal = financialGoalRepository.findByIdAndDashboardId(goalId, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("FinancialGoal not found with id: " + goalId + " for dashboard id: " + dashboardId));
        financialGoalRepository.delete(financialGoal);
    }
}