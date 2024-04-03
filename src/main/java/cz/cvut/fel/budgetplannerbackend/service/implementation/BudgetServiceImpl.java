package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.BudgetDto;
import cz.cvut.fel.budgetplannerbackend.entity.Budget;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.BudgetMapper;
import cz.cvut.fel.budgetplannerbackend.repository.BudgetRepository;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final DashboardRepository dashboardRepository;
    private final BudgetMapper budgetMapper;
    private static final Logger LOG = LoggerFactory.getLogger(BudgetServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDto> findAllBudgetsByDashboardId(Long dashboardId) {
        LOG.info("Fetching all budgets for dashboard id: {}", dashboardId);
        List<Budget> budgets = budgetRepository.findAllByDashboardId(dashboardId);
        return budgets.stream()
                .map(budgetMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetDto findBudgetByIdAndDashboardId(Long id, Long dashboardId) {
        LOG.info("Fetching budget with id: {} for dashboard id: {}", id, dashboardId);
        Budget budget = budgetRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id + " for dashboard id: " + dashboardId));
        return budgetMapper.toDto(budget);
    }

    @Override
    @Transactional
    public BudgetDto createBudget(Long dashboardId, BudgetDto budgetDto) {
        LOG.info("Creating new budget for dashboard id: {}", dashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Dashboard", dashboardId));
        Budget budget = budgetMapper.toEntity(budgetDto);
        budget.setDashboard(dashboard);
        Budget savedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(savedBudget);
    }

    @Override
    @Transactional
    public BudgetDto updateBudget(Long dashboardId, Long id, BudgetDto budgetDto) {
        LOG.info("Updating budget with id: {} for dashboard id: {}", id, dashboardId);
        Budget budget = budgetRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id + " for dashboard id: " + dashboardId));

        // Update only non-null fields from budgetDto
        if (budgetDto.title() != null) budget.setTitle(budgetDto.title());
        if (budgetDto.totalAmount() != null) budget.setTotalAmount(budgetDto.totalAmount());
        if (budgetDto.startDate() != null) budget.setStartDate(budgetDto.startDate());
        if (budgetDto.endDate() != null) budget.setEndDate(budgetDto.endDate());

        Budget updatedBudget = budgetRepository.save(budget);
        LOG.info("Updated budget with id: {} for dashboard id: {}", id, dashboardId);
        return budgetMapper.toDto(updatedBudget);
    }

    @Override
    @Transactional
    public void deleteBudget(Long dashboardId, Long id) {
        LOG.info("Deleting budget with id: {} for dashboard id: {}", id, dashboardId);
        Budget budget = budgetRepository.findByIdAndDashboardId(id, dashboardId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id + " for dashboard id: " + dashboardId));
        budgetRepository.delete(budget);
    }
}
