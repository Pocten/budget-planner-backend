package cz.cvut.fel.budgetplannerbackend.controller;


import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.dto.members.DashboardMemberDto;
import cz.cvut.fel.budgetplannerbackend.dto.members.MemberRequestDto;
import cz.cvut.fel.budgetplannerbackend.dto.RoleDto;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.implementation.DashboardAccessServiceImpl;
import cz.cvut.fel.budgetplannerbackend.service.implementation.DashboardRoleServiceImpl;
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
    private final DashboardRoleServiceImpl dashboardRoleService;
    private final DashboardAccessServiceImpl dashboardAccessService;

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
        List<DashboardDto> dashboards = dashboardService.findAllDashboardsByUserId(userId);
        LOG.info("Returned all dashboards for user id: {}", userId);
        return ResponseEntity.ok(dashboards);
    }

    @GetMapping("/{dashboardId}")
    public ResponseEntity<DashboardDto> getUserDashboardById(@PathVariable Long userId, @PathVariable Long dashboardId) {
        LOG.info("Received request to get dashboard with id: {} for user id: {}", dashboardId, userId);
        try {
            DashboardDto dashboardDto = dashboardService.findUserDashboardById(userId, dashboardId);
            LOG.info("Returned dashboard with id: {} for user id: {}", dashboardId, userId);
            return ResponseEntity.ok(dashboardDto);
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting dashboard with id: {} for user id: {}", dashboardId, userId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/accessible")
    public ResponseEntity<List<DashboardDto>> getAccessibleDashboards(@PathVariable Long userId) {
        LOG.info("Received request to get accessible dashboards for user id: {}", userId);
        try {
            List<DashboardDto> accessibleDashboards = dashboardService.findAccessibleDashboards()
                    .stream()
                    .map(dashboard -> dashboardService.findDashboardById(dashboard.id()))
                    .toList();
            LOG.info("Returned accessible dashboards for user id: {}", userId);
            return ResponseEntity.ok(accessibleDashboards);
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting accessible dashboards for user id: {}", userId, e);
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

    @PostMapping("/{dashboardId}/assign-role")
    public ResponseEntity<?> assignRoleToUserInDashboard(@PathVariable Long userId, @PathVariable Long dashboardId, @RequestBody RoleDto roleDto) {
        try {
            ERole role = ERole.valueOf(String.valueOf(roleDto.role()));
            dashboardRoleService.assignRoleToUserInDashboard(userId, dashboardId, role);
            LOG.info("Role {} assigned or updated for user {} on dashboard {}", role, userId, dashboardId);
            return ResponseEntity.ok("Role assigned or updated successfully");
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid role value: {}", roleDto.role());
            return ResponseEntity.badRequest().body("Invalid role value: " + roleDto.role());
        } catch (EntityNotFoundException e) {
            LOG.error("Error assigning or updating role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOG.error("Unexpected error when assigning or updating role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not assign or update role due to an error: " + e.getMessage());
        }
    }

    @PostMapping("/{dashboardId}/members/add")
    public ResponseEntity<?> addMember(@PathVariable Long userId, @PathVariable Long dashboardId, @RequestBody MemberRequestDto memberRequest) {
        try {
            dashboardService.addMember(dashboardId, memberRequest.usernameOrEmail(), userId);
            LOG.info("User {} added to dashboard with id {} by user with id {}", memberRequest.usernameOrEmail(), dashboardId, userId);
            return ResponseEntity.ok("Member successfully added");
        } catch (EntityNotFoundException e) {
            LOG.error("Error adding member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOG.error("Unexpected error when adding member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding member: " + e.getMessage());
        }
    }

    @GetMapping("/{dashboardId}/members")
    public ResponseEntity<List<DashboardMemberDto>> getDashboardMembers(@PathVariable Long userId, @PathVariable Long dashboardId) {
        LOG.info("Request to get members for dashboard {} by user {}", dashboardId, userId);
        try {
            List<DashboardMemberDto> members = dashboardService.findMembersByDashboardId(dashboardId);
            LOG.info("Successfully retrieved {} members for dashboard {}", members.size(), dashboardId);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            LOG.error("Error retrieving members for dashboard {}: {}", dashboardId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{dashboardId}/members/{usernameOrEmail}/changeAccess")
    public ResponseEntity<?> changeAccessLevel(@PathVariable Long userId, @PathVariable Long dashboardId, @PathVariable String usernameOrEmail, @RequestBody EAccessLevel newAccessLevel) {
        try {
            dashboardService.changeAccessLevel(dashboardId, usernameOrEmail, userId, newAccessLevel);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{dashboardId}/members/remove")
    public ResponseEntity<?> removeMember(@PathVariable Long userId, @PathVariable Long dashboardId, @RequestBody MemberRequestDto memberRequest) {
        try {
            dashboardService.removeMember(dashboardId, memberRequest.usernameOrEmail(), userId);
            LOG.info("User {} removed from dashboard {} by user {}", memberRequest.usernameOrEmail(), dashboardId, userId);
            return ResponseEntity.ok("Member successfully removed");
        } catch (EntityNotFoundException e) {
            LOG.error("Error removing member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOG.error("Unexpected error when removing member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing member: " + e.getMessage());
        }
    }
}