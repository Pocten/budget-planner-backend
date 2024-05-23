package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.service.implementation.FinancialRecordServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * REST controller for managing financial records within a dashboard.
 * Provides endpoints for CRUD (Create, Read, Update, Delete) operations on financial records.
 */
@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/financial-records") // Base URL for all financial record endpoints.
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordServiceImpl financialRecordService; // Service for handling financial record operations.
    private static final Logger LOG = LoggerFactory.getLogger(FinancialRecordController.class);

    /**
     * Retrieves a list of all financial records associated with a specific dashboard.
     *
     * @param dashboardId The ID of the dashboard.
     * @return A ResponseEntity containing a list of FinancialRecordDto objects and an HTTP status of 200 OK.
     * @throws AccessDeniedException If the user does not have at least VIEWER access to the dashboard.
     */
    @GetMapping
    public ResponseEntity<List<FinancialRecordDto>> getAllFinancialRecordsByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to list all financial records for dashboard id: {}", dashboardId);
        List<FinancialRecordDto> records = financialRecordService.findAllFinancialRecordsByDashboardId(dashboardId); // Retrieve the financial records.
        LOG.info("Returned all financial records for dashboard id: {}", dashboardId);
        return ResponseEntity.ok(records); // Return the records with an OK status.
    }

    /**
     * Retrieves a specific financial record by its ID and dashboard ID.
     *
     * @param dashboardId The ID of the dashboard.
     * @param id          The ID of the financial record.
     * @return A ResponseEntity containing the FinancialRecordDto and an HTTP status of 200 OK.
     * @throws EntityNotFoundException If the financial record is not found.
     * @throws AccessDeniedException If the user does not have at least VIEWER access to the dashboard.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecordDto> getFinancialRecordByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to fetch financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecordDto recordDto = financialRecordService.findFinancialRecordByIdAndDashboardId(id, dashboardId); // Retrieve the financial record.
        LOG.info("Returned financial record with id: {} for dashboard id: {}", id, dashboardId);
        return ResponseEntity.ok(recordDto); // Return the record with an OK status.
    }

    /**
     * Creates a new financial record within a dashboard.
     *
     * @param dashboardId          The ID of the dashboard.
     * @param financialRecordDto The FinancialRecordDto object containing the data for the new financial record.
     * @return A ResponseEntity containing the created FinancialRecordDto and an HTTP status of 201 Created.
     * @throws EntityNotFoundException If the dashboard or category (if provided) is not found.
     * @throws AccessDeniedException If the user does not have at least EDITOR access to the dashboard.
     */
    @PostMapping
    public ResponseEntity<FinancialRecordDto> createFinancialRecord(@PathVariable Long dashboardId, @RequestBody FinancialRecordDto financialRecordDto) {
        LOG.info("Received request to create a new financial record for dashboard id: {}", dashboardId);
        FinancialRecordDto createdRecord = financialRecordService.createFinancialRecord(dashboardId, financialRecordDto); // Create the financial record.
        LOG.info("Created new financial record with id: {} for dashboard id: {}", createdRecord.id(), dashboardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord); // Return the created record with a Created status.
    }

    /**
     * Updates an existing financial record.
     *
     * @param dashboardId          The ID of the dashboard.
     * @param id                   The ID of the financial record to update.
     * @param financialRecordDto The FinancialRecordDto object containing the updated data for the financial record.
     * @return A ResponseEntity containing the updated FinancialRecordDto and an HTTP status of 200 OK.
     * @throws EntityNotFoundException If the financial record is not found.
     * @throws AccessDeniedException If the user does not have at least EDITOR access to the dashboard.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecordDto> updateFinancialRecord(@PathVariable Long dashboardId, @PathVariable Long id, @RequestBody FinancialRecordDto financialRecordDto) {
        LOG.info("Received request to update financial record with id: {} for dashboard id: {}", id, dashboardId);
        FinancialRecordDto updatedRecord = financialRecordService.updateFinancialRecord(id, dashboardId, financialRecordDto); // Update the financial record.
        LOG.info("Updated financial record with id: {} for dashboard id: {}", id, dashboardId);
        return ResponseEntity.ok(updatedRecord); // Return the updated record with an OK status.
    }

    /**
     * Deletes a financial record.
     *
     * @param dashboardId The ID of the dashboard.
     * @param id          The ID of the financial record to delete.
     * @return A ResponseEntity with an HTTP status of 204 No Content.
     * @throws EntityNotFoundException If the financial record is not found.
     * @throws AccessDeniedException If the user does not have at least EDITOR access to the dashboard.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinancialRecord(@PathVariable Long dashboardId, @PathVariable Long id) {
        LOG.info("Received request to delete financial record with id: {} for dashboard id: {}", id, dashboardId);
        financialRecordService.deleteFinancialRecord(id, dashboardId); // Delete the financial record.
        LOG.info("Deleted financial record with id: {} for dashboard id: {}", id, dashboardId);
        return ResponseEntity.noContent().build(); // Return a No Content status to indicate successful deletion.
    }
}