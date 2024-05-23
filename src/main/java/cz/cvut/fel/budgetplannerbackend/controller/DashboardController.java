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
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * REST controller for managing dashboards for a specific user.
 * Provides endpoints for CRUD operations on dashboards, managing member access and roles,
 * and retrieving accessible dashboards.
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/dashboards") // Base URL for all dashboard-related endpoints.
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardServiceImpl dashboardService;
    private final DashboardRoleServiceImpl dashboardRoleService;
    private final DashboardAccessServiceImpl dashboardAccessService;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    /**
     * Creates a new dashboard for a specific user.
     *
     * @param userId       The ID of the user.
     * @param dashboardDto The DashboardDto object containing the data for the new dashboard.
     * @return A ResponseEntity containing the created DashboardDto object and an HTTP status of 201 Created.
     * @throws AccessDeniedException If the authenticated user is not authorized to access resources for the given userId.
     */
    @PostMapping
    public ResponseEntity<DashboardDto> createDashboard(@PathVariable Long userId, @RequestBody DashboardDto dashboardDto) {
        LOG.info("Received request to create a new dashboard for user id: {}", userId);
        DashboardDto createdDashboard = dashboardService.createDashboard(userId, dashboardDto); // Create the new dashboard using the service.
        LOG.info("Created dashboard with id: {} for user id: {}", createdDashboard.id(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDashboard); // Return the created dashboard DTO with a Created status.
    }

    /**
     * Retrieves all dashboards associated with a specific user.
     *
     * @param userId The ID of the user.
     * @return A ResponseEntity containing a list of DashboardDto objects and an HTTP status of 200 OK.
     * @throws AccessDeniedException If the authenticated user is not authorized to access resources for the given userId.
     */
    @GetMapping
    public ResponseEntity<List<DashboardDto>> getAllDashboardsByUserId(@PathVariable Long userId) {
        LOG.info("Received request to get all dashboards for user id: {}", userId);
        List<DashboardDto> dashboards = dashboardService.findAllDashboardsByUserId(userId); // Retrieve the dashboards using the service.
        LOG.info("Returned all dashboards for user id: {}", userId);
        return ResponseEntity.ok(dashboards); // Return the list of dashboards with an OK status.
    }

    /**
     * Retrieves a specific dashboard by its ID for a given user.
     *
     * @param userId      The ID of the user.
     * @param dashboardId The ID of the dashboard.
     * @return A ResponseEntity containing the DashboardDto object and an HTTP status of 200 OK if found,
     *         or 404 Not Found if not found.
     */
    @GetMapping("/{dashboardId}")
    public ResponseEntity<DashboardDto> getUserDashboardById(@PathVariable Long userId, @PathVariable Long dashboardId) {
        LOG.info("Received request to get dashboard with id: {} for user id: {}", dashboardId, userId);
        try {
            DashboardDto dashboardDto = dashboardService.findUserDashboardById(userId, dashboardId); // Retrieve the dashboard using the service.
            LOG.info("Returned dashboard with id: {} for user id: {}", dashboardId, userId);
            return ResponseEntity.ok(dashboardDto); // Return the dashboard with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting dashboard with id: {} for user id: {}", dashboardId, userId, e);
            return ResponseEntity.notFound().build(); // Return a Not Found status if the dashboard is not found.
        }
    }

    /**
     * Retrieves a list of dashboards accessible to the current user,
     * excluding dashboards created by the user themselves.
     *
     * @param userId The ID of the user.
     * @return A ResponseEntity containing a list of DashboardDto objects and an HTTP status of 200 OK if dashboards are found,
     *         or 404 Not Found if no accessible dashboards are found.
     */
    @GetMapping("/accessible")
    public ResponseEntity<List<DashboardDto>> getAccessibleDashboards(@PathVariable Long userId) {
        LOG.info("Received request to get accessible dashboards for user id: {}", userId);
        try {
            // Retrieve accessible dashboards and then fetch full details for each.
            List<DashboardDto> accessibleDashboards = dashboardService.findAccessibleDashboards()
                    .stream()
                    .map(dashboard -> dashboardService.findDashboardById(dashboard.id())) // Fetch full dashboard details using ID.
                    .toList();
            LOG.info("Returned accessible dashboards for user id: {}", userId);
            return ResponseEntity.ok(accessibleDashboards); // Return the list of dashboards with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting accessible dashboards for user id: {}", userId, e);
            return ResponseEntity.notFound().build(); // Return a Not Found status if no accessible dashboards are found.
        }
    }

    /**
     * Updates an existing dashboard for a specific user.
     *
     * @param userId       The ID of the user.
     * @param dashboardId  The ID of the dashboard to update.
     * @param dashboardDto The DashboardDto object containing the updated data for the dashboard.
     * @return A ResponseEntity containing the updated DashboardDto object and an HTTP status of 200 OK if successful,
     *         or 404 Not Found if the dashboard is not found.
     */
    @PutMapping("/{dashboardId}")
    public ResponseEntity<DashboardDto> updateDashboard(@PathVariable Long userId, @PathVariable Long dashboardId, @RequestBody DashboardDto dashboardDto) {
        LOG.info("Received request to update dashboard with id: {} for user id: {}", dashboardId, userId);
        try {
            DashboardDto updatedDashboard = dashboardService.updateDashboard(userId, dashboardId, dashboardDto); // Update the dashboard using the service.
            LOG.info("Updated dashboard with id: {} for user id: {}", dashboardId, userId);
            return ResponseEntity.ok(updatedDashboard); // Return the updated dashboard with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating dashboard with id: {} for user id: {}", dashboardId, userId, e);
            return ResponseEntity.notFound().build(); // Return a Not Found status if the dashboard is not found.
        }
    }

    /**
     * Deletes a dashboard for a specific user.
     *
     * @param userId      The ID of the user.
     * @param dashboardId The ID of the dashboard to delete.
     * @return A ResponseEntity with an HTTP status of 204 No Content if successful,
     *         or 404 Not Found if the dashboard is not found.
     */
    @DeleteMapping("/{dashboardId}")
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long userId, @PathVariable Long dashboardId) {
        LOG.info("Received request to delete dashboard with id: {} for user id: {}", dashboardId, userId);
        try {
            dashboardService.deleteDashboard(userId, dashboardId); // Delete the dashboard using the service.
            LOG.info("Deleted dashboard with id: {} for user id: {}", dashboardId, userId);
            return ResponseEntity.noContent().build(); // Return a No Content status to indicate successful deletion.
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting dashboard with id: {} for user id: {}", dashboardId, userId, e);
            return ResponseEntity.notFound().build(); // Return a Not Found status if the dashboard is not found.
        }
    }

    /**
     * Assigns or updates a role for a user on a specific dashboard.
     *
     * @param userId      The ID of the user.
     * @param dashboardId The ID of the dashboard.
     * @param roleDto     The RoleDto object containing the role to assign.
     * @return A ResponseEntity with an HTTP status of 200 OK if successful,
     *         400 Bad Request if the role value is invalid,
     *         404 Not Found if the user, dashboard, or role is not found,
     *         or 500 Internal Server Error if an unexpected error occurs.
     */
    @PostMapping("/{dashboardId}/assign-role")
    public ResponseEntity<?> assignRoleToUserInDashboard(@PathVariable Long userId, @PathVariable Long dashboardId, @RequestBody RoleDto roleDto) {
        try {
            ERole role = ERole.valueOf(String.valueOf(roleDto.role())); // Convert the role from the DTO to an ERole enum.
            dashboardRoleService.assignRoleToUserInDashboard(userId, dashboardId, role); // Assign the role using the service.
            LOG.info("Role {} assigned or updated for user {} on dashboard {}", role, userId, dashboardId);
            return ResponseEntity.ok("Role assigned or updated successfully"); // Return an OK status with a success message.
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid role value: {}", roleDto.role());
            return ResponseEntity.badRequest().body("Invalid role value: " + roleDto.role()); // Return a Bad Request status with an error message.
        } catch (EntityNotFoundException e) {
            LOG.error("Error assigning or updating role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Return a Not Found status with the exception message.
        } catch (Exception e) {
            LOG.error("Unexpected error when assigning or updating role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not assign or update role due to an error: " + e.getMessage()); // Return an Internal Server Error status with an error message.
        }
    }

    /**
     * Adds a member to a dashboard.
     *
     * @param userId         The ID of the user initiating the add operation.
     * @param dashboardId     The ID of the dashboard to add the member to.
     * @param memberRequest The MemberRequestDto object containing the username or email of the member to add.
     * @return A ResponseEntity with an HTTP status of 200 OK if successful,
     *         404 Not Found if the user or dashboard is not found,
     *         or 500 Internal Server Error if an unexpected error occurs.
     */
    @PostMapping("/{dashboardId}/members/add")
    public ResponseEntity<?> addMember(@PathVariable Long userId, @PathVariable Long dashboardId, @RequestBody MemberRequestDto memberRequest) {
        try {
            dashboardService.addMember(dashboardId, memberRequest.usernameOrEmail(), userId); // Add the member using the service.
            LOG.info("User {} added to dashboard with id {} by user with id {}", memberRequest.usernameOrEmail(), dashboardId, userId);
            return ResponseEntity.ok("Member successfully added"); // Return an OK status with a success message.
        } catch (EntityNotFoundException e) {
            LOG.error("Error adding member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Return a Not Found status with the exception message.
        } catch (Exception e) {
            LOG.error("Unexpected error when adding member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding member: " + e.getMessage()); // Return an Internal Server Error status with an error message.
        }
    }

    /**
     * Retrieves a list of members for a given dashboard.
     *
     * @param userId      The ID of the user initiating the request (for logging and security purposes).
     * @param dashboardId The ID of the dashboard.
     * @return A ResponseEntity containing a list of DashboardMemberDto objects and an HTTP status of 200 OK if successful,
     *         or 500 Internal Server Error if an error occurs.
     */
    @GetMapping("/{dashboardId}/members")
    public ResponseEntity<List<DashboardMemberDto>> getDashboardMembers(@PathVariable Long userId, @PathVariable Long dashboardId) {
        LOG.info("Request to get members for dashboard {} by user {}", dashboardId, userId);
        try {
            List<DashboardMemberDto> members = dashboardService.findMembersByDashboardId(dashboardId); // Retrieve the members using the service.
            LOG.info("Successfully retrieved {} members for dashboard {}", members.size(), dashboardId);
            return ResponseEntity.ok(members); // Return the list of members with an OK status.
        } catch (Exception e) {
            LOG.error("Error retrieving members for dashboard {}: {}", dashboardId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return an Internal Server Error status if an error occurs.
        }
    }

    /**
     * Changes the access level of a member on a dashboard.
     *
     * @param userId           The ID of the user initiating the change.
     * @param dashboardId        The ID of the dashboard.
     * @param usernameOrEmail     The username or email of the member whose access level is being changed.
     * @param newAccessLevel The new access level for the member.
     * @return A ResponseEntity with an HTTP status of 200 OK if successful,
     *         or 400 Bad Request if an error occurs.
     */
    @PutMapping("/{dashboardId}/members/{usernameOrEmail}/changeAccess")
    public ResponseEntity<?> changeAccessLevel(@PathVariable Long userId, @PathVariable Long dashboardId, @PathVariable String usernameOrEmail, @RequestBody EAccessLevel newAccessLevel) {
        try {
            dashboardService.changeAccessLevel(dashboardId, usernameOrEmail, userId, newAccessLevel); // Change the access level using the service.
            return new ResponseEntity<>(HttpStatus.OK); // Return an OK status if successful.
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // Return a Bad Request status with the error message if an error occurs.
        }
    }

    /**
     * Removes a member from a dashboard.
     *
     * @param userId         The ID of the user initiating the remove operation.
     * @param dashboardId     The ID of the dashboard.
     * @param memberRequest The MemberRequestDto object containing the username or email of the member to remove.
     * @return A ResponseEntity with an HTTP status of 200 OK if successful,
     *         404 Not Found if the user or dashboard is not found,
     *         or 500 Internal Server Error if an unexpected error occurs.
     */
    @DeleteMapping("/{dashboardId}/members/remove")
    public ResponseEntity<?> removeMember(@PathVariable Long userId, @PathVariable Long dashboardId, @RequestBody MemberRequestDto memberRequest) {
        try {
            dashboardService.removeMember(dashboardId, memberRequest.usernameOrEmail(), userId); // Remove the member using the service.
            LOG.info("User {} removed from dashboard {} by user {}", memberRequest.usernameOrEmail(), dashboardId, userId);
            return ResponseEntity.ok("Member successfully removed"); // Return an OK status with a success message.
        } catch (EntityNotFoundException e) {
            LOG.error("Error removing member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Return a Not Found status with the exception message.
        } catch (Exception e) {
            LOG.error("Unexpected error when removing member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing member: " + e.getMessage()); // Return an Internal Server Error status with an error message.
        }
    }
}