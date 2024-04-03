package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.implementation.FinancialGoalServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/budgets/{budgetId}/financial-goals")
@RequiredArgsConstructor
public class FinancialGoalController {

    private final FinancialGoalServiceImpl financialGoalService;
    private static final Logger LOG = LoggerFactory.getLogger(FinancialGoalController.class);

    @GetMapping
    public ResponseEntity<List<FinancialGoalDto>> getAllFinancialGoalsByBudgetId(@PathVariable Long dashboardId, @PathVariable Long budgetId) {
        LOG.info("Received request to get all financial goals for dashboard with id: {} and budget id: {}", dashboardId, budgetId);
        List<FinancialGoalDto> financialGoalDtos = financialGoalService.findAllFinancialGoalsByBudgetId(dashboardId, budgetId);
        LOG.info("Returned all financial goals for dashboard with id: {} and budget id: {}", dashboardId, budgetId);
        return ResponseEntity.ok(financialGoalDtos);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<FinancialGoalDto> getFinancialGoalByIdAndBudgetId(@PathVariable Long dashboardId, @PathVariable Long budgetId, @PathVariable Long goalId) {
        LOG.info("Received request to get financial goal with id: {} for dashboard with id: {} and budget id: {}", goalId, dashboardId, budgetId);
        try {
            FinancialGoalDto financialGoalDto = financialGoalService.findFinancialGoalByIdAndBudgetId(dashboardId, budgetId, goalId);
            LOG.info("Returned financial goal with id: {} for dashboard with id: {} and budget id: {}", goalId, dashboardId, budgetId);
            return ResponseEntity.ok(financialGoalDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting financial goal", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FinancialGoalDto> createFinancialGoal(@PathVariable Long dashboardId, @PathVariable Long budgetId, @RequestBody FinancialGoalDto financialGoalDto) {
        LOG.info("Received request to create financial goal for dashboard with id: {} and budget id: {}", dashboardId, budgetId);
        FinancialGoalDto createdFinancialGoalDto = financialGoalService.createFinancialGoal(dashboardId, budgetId, financialGoalDto);
        LOG.info("Created financial goal for dashboard with id: {} and budget id: {}", dashboardId, budgetId);
        return new ResponseEntity<>(createdFinancialGoalDto, HttpStatus.CREATED);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<FinancialGoalDto> updateFinancialGoal(@PathVariable Long dashboardId, @PathVariable Long budgetId, @PathVariable Long goalId, @RequestBody FinancialGoalDto financialGoalDto) {
        LOG.info("Received request to update financial goal with id: {} for dashboard with id: {} and budget id: {}", goalId, dashboardId, budgetId);
        try {
            FinancialGoalDto updatedFinancialGoalDto = financialGoalService.updateFinancialGoal(dashboardId, budgetId, goalId, financialGoalDto);
            LOG.info("Updated financial goal with id: {} for dashboard with id: {} and budget id: {}", goalId, dashboardId, budgetId);
            return ResponseEntity.ok(updatedFinancialGoalDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating financial goal", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteFinancialGoal(@PathVariable Long dashboardId, @PathVariable Long budgetId, @PathVariable Long goalId) {
        LOG.info("Received request to delete financial goal with id: {} for dashboard with id: {} and budget id: {}", goalId, dashboardId, budgetId);
        try {
            financialGoalService.deleteFinancialGoal(dashboardId, budgetId, goalId);
            LOG.info("Deleted financial goal with id: {} for dashboard with id: {} and budget id: {}", goalId, dashboardId, budgetId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting financial goal", e);
            return ResponseEntity.notFound().build();
        }
    }
}

