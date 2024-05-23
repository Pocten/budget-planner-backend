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
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * REST controller for managing budgets within a dashboard.
 * This controller provides endpoints for CRUD (Create, Read, Update, Delete) operations on budgets.
 */
@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/budgets") // Base URL for all budget-related endpoints.
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetServiceImpl budgetService; // Service for handling budget operations.
    private static final Logger LOG = LoggerFactory.getLogger(BudgetController.class);

    /**
     * Retrieves all budgets associated with a specific dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A ResponseEntity containing a list of BudgetDto objects and an HTTP status of 200 OK.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @GetMapping
    public ResponseEntity<List<BudgetDto>> getAllBudgetsByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all budgets for dashboard with id: {}", dashboardId);
        List<BudgetDto> budgetDtos = budgetService.findAllBudgetsByDashboardId(dashboardId); // Retrieve the budgets using the service.
        LOG.info("Returned all budgets for dashboard with id: {}", dashboardId);
        return ResponseEntity.ok(budgetDtos); // Return the budgets with an OK status.
    }

    /**
     * Retrieves a specific budget by its ID and dashboard ID.
     *
     * @param dashboardId The ID of the dashboard.
     * @param id          The ID of the budget.
     * @return A ResponseEntity containing the BudgetDto object and an HTTP status of 200 OK if found,
     *         or 404 Not Found if not found.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BudgetDto> getBudgetByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to get budget with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            BudgetDto budgetDto = budgetService.findBudgetByIdAndDashboardId(id, dashboardId); // Retrieve the budget using the service.
            LOG.info("Returned budget with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(budgetDto); // Return the budget with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting budget", e); // Log the exception if the budget is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Creates a new budget for a dashboard.
     *
     * @param dashboardId The ID of the dashboard to associate the budget with.
     * @param budgetDto   The BudgetDto object containing the data for the new budget.
     * @return A ResponseEntity containing the created BudgetDto object and an HTTP status of 201 Created.
     * @throws EntityNotFoundException If the dashboard is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @PostMapping
    public ResponseEntity<BudgetDto> createBudget(@PathVariable Long dashboardId, @RequestBody BudgetDto budgetDto) {
        LOG.info("Received request to create budget for dashboard with id: {}", dashboardId);
        BudgetDto createdBudgetDto = budgetService.createBudget(dashboardId, budgetDto); // Create the budget using the service.
        LOG.info("Created budget for dashboard with id: {}", dashboardId);
        return new ResponseEntity<>(createdBudgetDto, HttpStatus.CREATED); // Return the created budget with a Created status.
    }

    /**
     * Updates an existing budget.
     *
     * @param dashboardId The ID of the dashboard associated with the budget.
     * @param id          The ID of the budget to update.
     * @param budgetDto   The BudgetDto object containing the updated data for the budget.
     * @return A ResponseEntity containing the updated BudgetDto object and an HTTP status of 200 OK if found,
     *         or 404 Not Found if not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BudgetDto> updateBudget(@PathVariable Long dashboardId, @PathVariable Long id, @RequestBody BudgetDto budgetDto) {
        LOG.info("Received request to update budget with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            BudgetDto updatedBudgetDto = budgetService.updateBudget(dashboardId, id, budgetDto); // Update the budget using the service.
            LOG.info("Updated budget with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.ok(updatedBudgetDto); // Return the updated budget with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating budget", e); // Log the exception if the budget is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Deletes a budget.
     *
     * @param dashboardId The ID of the dashboard associated with the budget.
     * @param id          The ID of the budget to delete.
     * @return A ResponseEntity with an HTTP status of 204 No Content if successful,
     *         or 404 Not Found if the budget is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to delete budget with id: {} for dashboard with id: {}", id, dashboardId);
        try {
            budgetService.deleteBudget(dashboardId, id); // Delete the budget using the service.
            LOG.info("Deleted budget with id: {} for dashboard with id: {}", id, dashboardId);
            return ResponseEntity.noContent().build(); // Return a No Content status to indicate successful deletion.
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting budget", e); // Log the exception if the budget is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }
}