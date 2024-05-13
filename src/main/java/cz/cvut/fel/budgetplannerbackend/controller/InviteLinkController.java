package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.InviteLinkDto;
import cz.cvut.fel.budgetplannerbackend.security.model.CustomUserDetails;
import cz.cvut.fel.budgetplannerbackend.service.implementation.InviteLinkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/dashboards/{dashboardId}/invite-links")
@RequiredArgsConstructor
public class InviteLinkController {

    private final InviteLinkServiceImpl inviteLinkService;

    @PostMapping
    public ResponseEntity<InviteLinkDto> createInviteLink(@PathVariable Long dashboardId) {
        InviteLinkDto newInviteLink = new InviteLinkDto(null, null, LocalDateTime.now().plusDays(30), true, dashboardId);
        InviteLinkDto createdInviteLinkDto = inviteLinkService.createInviteLink(newInviteLink);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInviteLinkDto);
    }

    @PutMapping("/activate/{link}")
    public ResponseEntity<String> activateLink(@PathVariable String link) {
        inviteLinkService.activateLink(link);
        return ResponseEntity.ok("Link activated successfully.");
    }

    @PutMapping("/deactivate/{link}")
    public ResponseEntity<String> deactivateLink(@PathVariable String link) {
        inviteLinkService.deactivateLink(link);
        return ResponseEntity.ok("Link deactivated successfully.");
    }

    @GetMapping("/use/{link}")
    public ResponseEntity<String> useLink(@PathVariable String link, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean accessGranted = inviteLinkService.useInviteLink(link, userDetails.getUserId());
            return ResponseEntity.ok(accessGranted ? "Access granted" : "Access denied");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User authentication required.");
    }
}
