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
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * REST controller for managing financial goals within a dashboard.
 * Provides endpoints for CRUD (Create, Read, Update, Delete) operations on financial goals.
 */
@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/financial-goals") // Base URL for all financial goal endpoints
@RequiredArgsConstructor
public class FinancialGoalController {

    private final FinancialGoalServiceImpl financialGoalService; // Service for handling financial goal operations.
    private static final Logger LOG = LoggerFactory.getLogger(FinancialGoalController.class);

    /**
     * Retrieves all financial goals associated with a specific dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A ResponseEntity containing a list of FinancialGoalDto objects and an HTTP status of 200 OK.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @GetMapping
    public ResponseEntity<List<FinancialGoalDto>> getAllFinancialGoalsByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all financial goals for dashboard with id: {}", dashboardId);
        List<FinancialGoalDto> financialGoalDtos = financialGoalService.findAllFinancialGoalsByDashboardId(dashboardId); // Retrieve all financial goals.
        LOG.info("Returned all financial goals for dashboard with id: {}", dashboardId);
        return ResponseEntity.ok(financialGoalDtos); // Return the goals with an OK status.
    }

    /**
     * Retrieves a specific financial goal by its ID and dashboard ID.
     *
     * @param dashboardId The ID of the dashboard.
     * @param goalId      The ID of the financial goal.
     * @return A ResponseEntity containing the FinancialGoalDto object and an HTTP status of 200 OK if found,
     *         or 404 Not Found if not found.
     * @throws AccessDeniedException If the user does not have viewer access to the dashboard.
     */
    @GetMapping("/{goalId}")
    public ResponseEntity<FinancialGoalDto> getFinancialGoalByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long goalId) {
        LOG.info("Received request to get financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
        try {
            FinancialGoalDto financialGoalDto = financialGoalService.findFinancialGoalByIdAndDashboardId(dashboardId, goalId); // Retrieve the financial goal.
            LOG.info("Returned financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
            return ResponseEntity.ok(financialGoalDto); // Return the goal with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting financial goal", e); // Log the exception if the goal is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Creates a new financial goal for a dashboard.
     *
     * @param dashboardId      The ID of the dashboard to associate the financial goal with.
     * @param financialGoalDto The FinancialGoalDto object containing the data for the new financial goal.
     * @return A ResponseEntity containing the created FinancialGoalDto object and an HTTP status of 201 Created.
     * @throws EntityNotFoundException If the dashboard is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @PostMapping
    public ResponseEntity<FinancialGoalDto> createFinancialGoal(@PathVariable Long dashboardId, @RequestBody FinancialGoalDto financialGoalDto) {
        LOG.info("Received request to create financial goal for dashboard with id: {}", dashboardId);
        FinancialGoalDto createdFinancialGoalDto = financialGoalService.createFinancialGoal(dashboardId, financialGoalDto); // Create the financial goal.
        LOG.info("Created financial goal for dashboard with id: {}", dashboardId);
        return new ResponseEntity<>(createdFinancialGoalDto, HttpStatus.CREATED); // Return the created goal with a Created status.
    }

    /**
     * Updates an existing financial goal.
     *
     * @param dashboardId      The ID of the dashboard associated with the financial goal.
     * @param goalId           The ID of the financial goal to update.
     * @param financialGoalDto The FinancialGoalDto object containing the updated data for the financial goal.
     * @return A ResponseEntity containing the updated FinancialGoalDto object and an HTTP status of 200 OK if found,
     *         or 404 Not Found if not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @PutMapping("/{goalId}")
    public ResponseEntity<FinancialGoalDto> updateFinancialGoal(@PathVariable Long dashboardId, @PathVariable Long goalId, @RequestBody FinancialGoalDto financialGoalDto) {
        LOG.info("Received request to update financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
        try {
            FinancialGoalDto updatedFinancialGoalDto = financialGoalService.updateFinancialGoal(dashboardId, goalId, financialGoalDto); // Update the financial goal.
            LOG.info("Updated financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
            return ResponseEntity.ok(updatedFinancialGoalDto); // Return the updated goal with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating financial goal", e); // Log the exception if the goal is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Deletes a financial goal.
     *
     * @param dashboardId The ID of the dashboard associated with the financial goal.
     * @param goalId      The ID of the financial goal to delete.
     * @return A ResponseEntity with an HTTP status of 204 No Content if successful,
     *         or 404 Not Found if the financial goal is not found.
     * @throws AccessDeniedException If the user does not have editor access to the dashboard.
     */
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteFinancialGoal(@PathVariable Long dashboardId, @PathVariable Long goalId) {
        LOG.info("Received request to delete financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
        try {
            financialGoalService.deleteFinancialGoal(dashboardId, goalId); // Delete the financial goal.
            LOG.info("Deleted financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
            return ResponseEntity.noContent().build(); // Return a No Content status to indicate successful deletion.
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting financial goal", e); // Log the exception if the goal is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }
}