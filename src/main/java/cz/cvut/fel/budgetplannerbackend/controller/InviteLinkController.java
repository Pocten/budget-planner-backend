package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.InviteLinkDto;
import cz.cvut.fel.budgetplannerbackend.security.model.CustomUserDetails;
import cz.cvut.fel.budgetplannerbackend.service.implementation.InviteLinkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for managing invite links for dashboards.
 * This controller provides endpoints for creating, activating, deactivating, and using invite links.
 */
@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/invite-links") // Base URL for all invite link related endpoints.
@RequiredArgsConstructor
public class InviteLinkController {

    private final InviteLinkServiceImpl inviteLinkService; // Service class for handling invite link operations.

    /**
     * Creates a new invite link for a dashboard.
     *
     * @param dashboardId The ID of the dashboard for which to create the invite link.
     * @return A ResponseEntity containing the created InviteLinkDto object and an HTTP status of 201 Created.
     */
    @PostMapping
    public ResponseEntity<InviteLinkDto> createInviteLink(@PathVariable Long dashboardId) {
        // Create a new InviteLinkDto object with default values (expiry date 30 days from now and active).
        InviteLinkDto newInviteLink = new InviteLinkDto(null, null, LocalDateTime.now().plusDays(30), true, dashboardId);
        InviteLinkDto createdInviteLinkDto = inviteLinkService.createInviteLink(newInviteLink); // Create the invite link using the service.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInviteLinkDto); // Return the created InviteLinkDto with a Created status.
    }

    /**
     * Activates an invite link.
     *
     * @param link The unique string representing the invite link.
     * @return A ResponseEntity with a success message and an HTTP status of 200 OK.
     */
    @PutMapping("/activate/{link}")
    public ResponseEntity<String> activateLink(@PathVariable String link) {
        inviteLinkService.activateLink(link); // Activate the link using the service.
        return ResponseEntity.ok("Link activated successfully."); // Return a success message with an OK status.
    }

    /**
     * Deactivates an invite link.
     *
     * @param link The unique string representing the invite link.
     * @return A ResponseEntity with a success message and an HTTP status of 200 OK.
     */
    @PutMapping("/deactivate/{link}")
    public ResponseEntity<String> deactivateLink(@PathVariable String link) {
        inviteLinkService.deactivateLink(link); // Deactivate the link using the service.
        return ResponseEntity.ok("Link deactivated successfully."); // Return a success message with an OK status.
    }

    /**
     * Uses an invite link to grant a user access to a dashboard.
     *
     * @param link           The unique string representing the invite link.
     * @param authentication The authentication object containing user details.
     * @return A ResponseEntity with a message indicating whether access was granted and an appropriate HTTP status.
     */
    @GetMapping("/use/{link}")
    public ResponseEntity<String> useLink(@PathVariable String link, Authentication authentication) {
        // Check if the user is authenticated.
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean accessGranted = inviteLinkService.useInviteLink(link, userDetails.getUserId()); // Attempt to use the link.
            return ResponseEntity.ok(accessGranted ? "Access granted" : "Access denied"); // Return a message indicating access status.
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User authentication required."); // Return a Forbidden status if not authenticated.
    }
}