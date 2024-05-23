package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.BudgetDto;
import cz.cvut.fel.budgetplannerbackend.entity.Budget;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.BudgetMapper;
import cz.cvut.fel.budgetplannerbackend.repository.BudgetRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.security.utils.SecurityUtils;
import cz.cvut.fel.budgetplannerbackend.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * Service class for managing budgets.
 */
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final DashboardRepository dashboardRepository;
    private final BudgetMapper budgetMapper;
    private final SecurityUtils securityUtils;

    private static final Logger LOG = LoggerFactory.getLogger(BudgetServiceImpl.class);

    /**
     * Retrieves all budgets associated with a specific dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A list of Budget DTOs representing the budgets.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BudgetDto> findAllBudgetsByDashboardId(Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching all budgets for dashboard id: {}", dashboardId);
        List<Budget> budgets = budgetRepository.findAllByDashboardId(dashboardId);
        return budgets.stream()
                .map(budgetMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a specific budget by its ID and dashboard ID.
     *
     * @param id          The ID of the budget.
     * @param dashboardId The ID of the dashboard.
     * @return The Budget DTO representing the budget.
     * @throws EntityNotFoundException If the budget is not found.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @Override
    @Transactional(readOnly = true)
    public BudgetDto findBudgetByIdAndDashboardId(Long id, Long dashboardId) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.VIEWER);
        LOG.info("Fetching budget with id: {} for dashboard id: {}", id, dashboardId);
        Budget budget = budgetRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id + " for dashboard id: " + dashboardId));
        return budgetMapper.toDto(budget);
    }

    /**
     * Creates a new budget and associates it with a specific dashboard.
     *
     * @param dashboardId The ID of the dashboard to associate the budget with.
     * @param budgetDto The Budget DTO containing the data for the new budget.
     * @return The Budget DTO representing the created budget.
     * @throws EntityNotFoundException If the dashboard is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @Override
    @Transactional
    public BudgetDto createBudget(Long dashboardId, BudgetDto budgetDto) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Creating new budget for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        Budget budget = budgetMapper.toEntity(budgetDto);
        budget.setDashboard(dashboard); // Associate the budget with the dashboard
        Budget savedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(savedBudget);
    }

    /**
     * Updates an existing budget.
     *
     * @param dashboardId The ID of the dashboard associated with the budget.
     * @param id          The ID of the budget to update.
     * @param budgetDto   The Budget DTO containing the updated data for the budget.
     * @return The Budget DTO representing the updated budget.
     * @throws EntityNotFoundException If the budget is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @Override
    @Transactional
    public BudgetDto updateBudget(Long dashboardId, Long id, BudgetDto budgetDto) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Updating budget with id: {} for dashboard id: {}", id, dashboardId);
        Budget budget = budgetRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id + " for dashboard id: " + dashboardId));

        // Update only non-null fields from the budgetDto
        if (budgetDto.title() != null) budget.setTitle(budgetDto.title());
        if (budgetDto.totalAmount() != null) budget.setTotalAmount(budgetDto.totalAmount());
        if (budgetDto.startDate() != null) budget.setStartDate(budgetDto.startDate());
        if (budgetDto.endDate() != null) budget.setEndDate(budgetDto.endDate());

        Budget updatedBudget = budgetRepository.save(budget);
        LOG.info("Updated budget with id: {} for dashboard id: {}", id, dashboardId);
        return budgetMapper.toDto(updatedBudget);
    }

    /**
     * Deletes a budget.
     *
     * @param dashboardId The ID of the dashboard associated with the budget.
     * @param id          The ID of the budget to delete.
     * @throws EntityNotFoundException If the budget is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @Override
    @Transactional
    public void deleteBudget(Long dashboardId, Long id) {
        securityUtils.checkDashboardAccess(dashboardId, EAccessLevel.EDITOR);
        LOG.info("Initiating deletion of budget with id: {} for dashboard id: {}", id, dashboardId);
        Budget budget = budgetRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id + " for dashboard id: " + dashboardId));

        budgetRepository.delete(budget); // Delete the budget.
        LOG.info("Budget with id: {} successfully deleted, along with all its associated financial goals.", id);
    }
}