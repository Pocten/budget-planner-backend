package cz.cvut.fel.budgetplannerbackend.controller;


import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.implementation.DashboardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardServiceImpl dashboardService;
    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    @PostMapping
    public ResponseEntity<DashboardDto> createDashboard(@PathVariable Long userId, @RequestBody DashboardDto dashboardDto) {
        LOG.info("Received request to create a new dashboard for user id: {}", userId);
        DashboardDto createdDashboard = dashboardService.createDashboard(userId, dashboardDto);
        LOG.info("Created dashboard with id: {} for user id: {}", createdDashboard.id(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDashboard);
    }

    @GetMapping
    public ResponseEntity<List<DashboardDto>> getAllDashboardsByUserId(@PathVariable Long userId) {
        LOG.info("Received request to get all dashboards for user id: {}", userId);
        List<DashboardDto> dashboards = dashboardService.getAllDashboardsByUserId(userId);
        LOG.info("Returned all dashboards for user id: {}", userId);
        return ResponseEntity.ok(dashboards);
    }

    @GetMapping("/{dashboardId}")
    public ResponseEntity<DashboardDto> getUserDashboardById(@PathVariable Long userId, @PathVariable Long dashboardId) {
        LOG.info("Received request to get dashboard with id: {} for user id: {}", dashboardId, userId);
        try {
            DashboardDto dashboardDto = dashboardService.getUserDashboardById(userId, dashboardId);
            LOG.info("Returned dashboard with id: {} for user id: {}", dashboardId, userId);
            return ResponseEntity.ok(dashboardDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting dashboard with id: {} for user id: {}", dashboardId, userId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{dashboardId}")
    public ResponseEntity<DashboardDto> updateDashboard(@PathVariable Long userId, @PathVariable Long dashboardId, @RequestBody DashboardDto dashboardDto) {
        LOG.info("Received request to update dashboard with id: {} for user id: {}", dashboardId, userId);
        try {
            DashboardDto updatedDashboard = dashboardService.updateDashboard(userId, dashboardId, dashboardDto);
            LOG.info("Updated dashboard with id: {} for user id: {}", dashboardId, userId);
            return ResponseEntity.ok(updatedDashboard);
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating dashboard with id: {} for user id: {}", dashboardId, userId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{dashboardId}")
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long userId, @PathVariable Long dashboardId) {
        LOG.info("Received request to delete dashboard with id: {} for user id: {}", dashboardId, userId);
        try {
            dashboardService.deleteDashboard(userId, dashboardId);
            LOG.info("Deleted dashboard with id: {} for user id: {}", dashboardId, userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting dashboard with id: {} for user id: {}", dashboardId, userId, e);
            return ResponseEntity.notFound().build();
        }
    }
}
