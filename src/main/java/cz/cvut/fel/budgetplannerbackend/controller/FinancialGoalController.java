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
@RequestMapping("/api/v1/dashboards/{dashboardId}/financial-goals")
@RequiredArgsConstructor
public class FinancialGoalController {

    private final FinancialGoalServiceImpl financialGoalService;
    private static final Logger LOG = LoggerFactory.getLogger(FinancialGoalController.class);

    @GetMapping
    public ResponseEntity<List<FinancialGoalDto>> getAllFinancialGoalsByDashboardId(@PathVariable Long dashboardId) {
        LOG.info("Received request to get all financial goals for dashboard with id: {}", dashboardId);
        List<FinancialGoalDto> financialGoalDtos = financialGoalService.findAllFinancialGoalsByDashboardId(dashboardId);
        LOG.info("Returned all financial goals for dashboard with id: {}", dashboardId);
        return ResponseEntity.ok(financialGoalDtos);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<FinancialGoalDto> getFinancialGoalByIdAndDashboardId(@PathVariable Long dashboardId, @PathVariable Long goalId) {
        LOG.info("Received request to get financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
        try {
            FinancialGoalDto financialGoalDto = financialGoalService.findFinancialGoalByIdAndDashboardId(dashboardId, goalId);
            LOG.info("Returned financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
            return ResponseEntity.ok(financialGoalDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting financial goal", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FinancialGoalDto> createFinancialGoal(@PathVariable Long dashboardId, @RequestBody FinancialGoalDto financialGoalDto) {
        LOG.info("Received request to create financial goal for dashboard with id: {}", dashboardId);
        FinancialGoalDto createdFinancialGoalDto = financialGoalService.createFinancialGoal(dashboardId, financialGoalDto);
        LOG.info("Created financial goal for dashboard with id: {}", dashboardId);
        return new ResponseEntity<>(createdFinancialGoalDto, HttpStatus.CREATED);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<FinancialGoalDto> updateFinancialGoal(@PathVariable Long dashboardId, @PathVariable Long goalId, @RequestBody FinancialGoalDto financialGoalDto) {
        LOG.info("Received request to update financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
        try {
            FinancialGoalDto updatedFinancialGoalDto = financialGoalService.updateFinancialGoal(dashboardId, goalId, financialGoalDto);
            LOG.info("Updated financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
            return ResponseEntity.ok(updatedFinancialGoalDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating financial goal", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteFinancialGoal(@PathVariable Long dashboardId, @PathVariable Long goalId) {
        LOG.info("Received request to delete financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
        try {
            financialGoalService.deleteFinancialGoal(dashboardId, goalId);
            LOG.info("Deleted financial goal with id: {} for dashboard with id: {}", goalId, dashboardId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting financial goal", e);
            return ResponseEntity.notFound().build();
        }
    }
}
