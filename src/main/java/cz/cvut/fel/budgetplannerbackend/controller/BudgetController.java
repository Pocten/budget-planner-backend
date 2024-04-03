package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.BudgetDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.implementation.BudgetServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetServiceImpl budgetService;
    private static final Logger LOG = LoggerFactory.getLogger(BudgetController.class);

    @GetMapping
    public ResponseEntity<List<BudgetDto>> getAllBudgetsByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all budgets for dashboard with id: {}", dashboardId);
        List<BudgetDto> budgetDtos = budgetService.findAllBudgetsByDashboardId(dashboardId);
        LOG.info("Returned all budgets for dashboard with id: {}", dashboardId);
        return ResponseEntity.ok(budgetDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDto> getBudgetByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to get budget with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            BudgetDto budgetDto = budgetService.findBudgetByIdAndDashboardId(id, dashboardId);
            LOG.info("Returned budget with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(budgetDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting budget", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<BudgetDto> createBudget(@PathVariable Long dashboardId, @RequestBody BudgetDto budgetDto) {
        LOG.info("Received request to create budget for dashboard with id: {}", dashboardId);
        BudgetDto createdBudgetDto = budgetService.createBudget(dashboardId, budgetDto);
        LOG.info("Created budget for dashboard with id: {}", dashboardId);
        return new ResponseEntity<>(createdBudgetDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDto> updateBudget(@PathVariable Long dashboardId, @PathVariable Long id, @RequestBody BudgetDto budgetDto) {
        LOG.info("Received request to update budget with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            BudgetDto updatedBudgetDto = budgetService.updateBudget(dashboardId, id, budgetDto);
            LOG.info("Updated budget with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(updatedBudgetDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating budget", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to delete budget with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            budgetService.deleteBudget(dashboardId, id);
            LOG.info("Deleted budget with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting budget", e);
            return ResponseEntity.notFound().build();
        }
    }
}
