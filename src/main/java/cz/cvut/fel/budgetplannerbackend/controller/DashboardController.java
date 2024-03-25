package cz.cvut.fel.budgetplannerbackend.controller;


import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.dashboard.DashboardNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    @PostMapping
    public ResponseEntity<DashboardDto> createDashboard(@RequestBody DashboardDto dashboardDto) {
        LOG.info("Received request to create a new dashboard");
        DashboardDto createdDashboard = dashboardService.createDashboard(dashboardDto);
        LOG.info("Created dashboard with id: {}", createdDashboard.id());
        return ResponseEntity.status(201).body(createdDashboard);
    }

    @GetMapping
    public ResponseEntity<List<DashboardDto>> getAllDashboardsByUserId(@RequestParam Long userId) { // Измененный метод для получения userId из параметров запроса
        LOG.info("Received request to get all dashboards for user id: {}", userId);
        List<DashboardDto> dashboards = dashboardService.getAllDashboardsByUserId(userId);
        LOG.info("Returned all dashboards for user id: {}", userId);
        return ResponseEntity.ok(dashboards);
    }

    @GetMapping("/{dashboardId}")
    public ResponseEntity<DashboardDto> getDashboardById(@PathVariable Long dashboardId) {
        LOG.info("Received request to get dashboard with id: {}", dashboardId);
        try {
            DashboardDto dashboardDto = dashboardService.getDashboardById(dashboardId);
            LOG.info("Returned dashboard with id: {}", dashboardId);
            return ResponseEntity.ok(dashboardDto);
        } catch (DashboardNotFoundException e) {
            LOG.error("Error getting dashboard", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{dashboardId}")
    public ResponseEntity<DashboardDto> updateDashboard(@PathVariable Long dashboardId, @RequestBody DashboardDto dashboardDto) {
        LOG.info("Received request to update dashboard with id: {}", dashboardId);
        try {
            DashboardDto updatedDashboard = dashboardService.updateDashboard(dashboardId, dashboardDto);
            LOG.info("Updated dashboard with id: {}", dashboardId);
            return ResponseEntity.ok(updatedDashboard);
        } catch (DashboardNotFoundException e) {
            LOG.error("Error updating dashboard", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{dashboardId}")
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long dashboardId) {
        LOG.info("Received request to delete dashboard with id: {}", dashboardId);
        try {
            dashboardService.deleteDashboard(dashboardId);
            LOG.info("Deleted dashboard with id: {}", dashboardId);
            return ResponseEntity.noContent().build();
        } catch (DashboardNotFoundException e) {
            LOG.error("Error deleting dashboard", e);
            return ResponseEntity.notFound().build();
        }
    }
}
