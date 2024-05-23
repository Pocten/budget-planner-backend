package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.InviteLinkDto;
import cz.cvut.fel.budgetplannerbackend.entity.InviteLink;
import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.InviteLinkMapper;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.InviteLinkRepository;
import cz.cvut.fel.budgetplannerbackend.service.InviteLinkService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing invite links.
 */
@Service
@RequiredArgsConstructor
public class InviteLinkServiceImpl implements InviteLinkService {

    private final InviteLinkRepository inviteLinkRepository;
    private final InviteLinkMapper inviteLinkMapper;
    private final DashboardAccessServiceImpl dashboardAccessService;
    private final DashboardRoleServiceImpl dashboardRoleService;
    private final DashboardRepository dashboardRepository;

    private static final Logger LOG = LoggerFactory.getLogger(InviteLinkServiceImpl.class);

    /**
     * Creates a new invite link for a dashboard.
     * If an active link already exists for the dashboard, it is deleted.
     *
     * @param inviteLinkDto The DTO object containing the dashboard ID.
     * @return The created invite link DTO.
     */
    @Override
    @Transactional
    public InviteLinkDto createInviteLink(InviteLinkDto inviteLinkDto) {
        // Check if there's already an active invite link for the given dashboard.
        Optional<InviteLink> existingLink = inviteLinkRepository.findByDashboardIdAndIsActiveTrue(inviteLinkDto.dashboardId());
        existingLink.ifPresent(inviteLinkRepository::delete); // Delete the existing link if found.

        // Create a new invite link.
        InviteLink newLink = new InviteLink();
        newLink.setLink(UUID.randomUUID().toString()); // Generate a unique link using UUID.
        newLink.setDashboard(dashboardRepository.findById(inviteLinkDto.dashboardId()).orElseThrow()); // Fetch the dashboard or throw an exception.
        newLink.setExpiryDate(LocalDateTime.now().plusDays(30)); // Set expiry date 30 days from now.
        newLink.setActive(true); // Set the link as active.
        inviteLinkRepository.save(newLink); // Save the new invite link to the database.

        return inviteLinkMapper.toDto(newLink); // Return the DTO representation of the new link.
    }

    /**
     * Scheduled task to check and refresh expired invite links every 24 hours.
     */
    @Override
    @Scheduled(fixedRate = 86400000) // Run every 24 hours (86400000 milliseconds).
    public void checkAndRefreshLinks() {
        LOG.debug("Checking and refreshing expired invite links.");

        // Find all expired invite links.
        List<InviteLink> expiredLinks = inviteLinkRepository.findExpiredLinks(LocalDateTime.now());

        // Refresh each expired link.
        expiredLinks.forEach(link -> {
            String newLink = UUID.randomUUID().toString(); // Generate a new unique link.
            link.setLink(newLink); // Update the link.
            LocalDateTime newExpiryDate = LocalDateTime.now().plusDays(30); // Set a new expiry date 30 days from now.
            link.setExpiryDate(newExpiryDate);
            link.setActive(true); // Set the link as active.
            inviteLinkRepository.save(link); // Save the refreshed link to the database.
            LOG.info("Refreshed invite link: {} with new expiry date: {}", newLink, newExpiryDate);
        });
    }

    /**
     * Activates an invite link.
     *
     * @param link The invite link to activate.
     * @throws EntityNotFoundException If the invite link is not found.
     */
    @Override
    @Transactional
    public void activateLink(String link) {
        LOG.debug("Activating invite link: {}", link);
        // Find the invite link by its unique identifier.
        InviteLink inviteLink = inviteLinkRepository.findByLink(link)
                .orElseThrow(() -> new EntityNotFoundException("Invite link not found: " + link));

        inviteLink.setActive(true); // Activate the link.
        inviteLinkRepository.save(inviteLink); // Save the changes to the database.
        LOG.info("Activated invite link: {}", link);
    }

    /**
     * Deactivates an invite link.
     *
     * @param link The invite link to deactivate.
     * @throws EntityNotFoundException If the invite link is not found.
     */
    @Override
    @Transactional
    public void deactivateLink(String link) {
        LOG.debug("Deactivating invite link: {}", link);
        // Find the invite link by its unique identifier.
        InviteLink inviteLink = inviteLinkRepository.findByLink(link)
                .orElseThrow(() -> new EntityNotFoundException("Invite link not found: " + link));

        inviteLink.setActive(false); // Deactivate the link.
        inviteLinkRepository.save(inviteLink); // Save the changes to the database.
        LOG.info("Deactivated invite link: {}", link);
    }

    /**
     * Uses an invite link to grant a user access to a dashboard.
     *
     * @param link   The invite link to use.
     * @param userId The ID of the user to grant access to.
     * @return True if the link was successfully used, false otherwise.
     * @throws EntityNotFoundException If the invite link is not found or not active.
     * @throws IllegalStateException    If the invite link is expired.
     */
    @Override
    @Transactional
    public boolean useInviteLink(String link, Long userId) {
        LOG.debug("User {} is attempting to use invite link: {}", userId, link);

        // Retrieve the invite link, ensuring it's active.
        InviteLink inviteLink = inviteLinkRepository.findByLink(link)
                .filter(InviteLink::isActive) // Only consider active links.
                .orElseThrow(() -> new EntityNotFoundException("Active invite link not found: " + link));

        // Check if the link is not expired.
        if (inviteLink.getExpiryDate().isAfter(LocalDateTime.now())) {
            // Grant VIEWER access to the user for the dashboard associated with the invite link.
            dashboardAccessService.grantAccess(userId, inviteLink.getDashboard().getId(), EAccessLevel.VIEWER);
            LOG.info("Granted access to user {} for dashboard ID {} via invite link {}", userId, inviteLink.getDashboard().getId(), link);

            // Assign the NONE role to the user for the dashboard.
            dashboardRoleService.assignRoleToUserInDashboard(userId, inviteLink.getDashboard().getId(), ERole.NONE);
            LOG.info("Assigned role to user {} for dashboard ID {} via invite link {}", userId, inviteLink.getDashboard().getId(), link);

            return true; // Indicate successful link usage.
        } else {
            LOG.warn("Attempt to use expired link: {}", link);
            throw new IllegalStateException("Link is expired");
        }
    }
}