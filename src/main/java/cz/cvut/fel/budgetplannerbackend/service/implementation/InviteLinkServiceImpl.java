package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.InviteLinkDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
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

@Service
@RequiredArgsConstructor
public class InviteLinkServiceImpl implements InviteLinkService {

    private final InviteLinkRepository inviteLinkRepository;
    private final InviteLinkMapper inviteLinkMapper;
    private final DashboardAccessServiceImpl dashboardAccessService;
    private final DashboardRoleServiceImpl dashboardRoleService;
    private final DashboardRepository dashboardRepository;

    private static final Logger LOG = LoggerFactory.getLogger(InviteLinkServiceImpl.class);

    @Override
    @Transactional
    public InviteLinkDto createInviteLink(InviteLinkDto inviteLinkDto) {
        // Checking if there is already an active link for this dashboard
        Optional<InviteLink> existingLink = inviteLinkRepository.findByDashboardIdAndIsActiveTrue(inviteLinkDto.dashboardId());
        // Removing an existing link
        existingLink.ifPresent(inviteLinkRepository::delete);

        // Create a new link
        InviteLink newLink = new InviteLink();
        newLink.setLink(UUID.randomUUID().toString()); // Generating a unique link
        newLink.setDashboard(dashboardRepository.findById(inviteLinkDto.dashboardId()).orElseThrow());
        newLink.setExpiryDate(LocalDateTime.now().plusDays(30));
        newLink.setActive(true);
        inviteLinkRepository.save(newLink);

        return inviteLinkMapper.toDto(newLink);
    }


    @Override
    @Scheduled(fixedRate = 86400000) // 86400000 milliseconds = 24 hours
    public void checkAndRefreshLinks() {
        LOG.debug("Checking and refreshing expired invite links.");
        List<InviteLink> expiredLinks = inviteLinkRepository.findExpiredLinks(LocalDateTime.now());
        expiredLinks.forEach(link -> {
            String newLink = UUID.randomUUID().toString();
            link.setLink(newLink);
            LocalDateTime newExpiryDate = LocalDateTime.now().plusDays(30);
            link.setExpiryDate(newExpiryDate);
            link.setActive(true);
            inviteLinkRepository.save(link);
            LOG.info("Refreshed invite link: {} with new expiry date: {}", newLink, newExpiryDate);
        });
    }

    @Override
    @Transactional
    public void activateLink(String link) {
        LOG.debug("Activating invite link: {}", link);
        InviteLink inviteLink = inviteLinkRepository.findByLink(link)
                .orElseThrow(() -> new EntityNotFoundException("Invite link not found: " + link));
        inviteLink.setActive(true);
        inviteLinkRepository.save(inviteLink);
        LOG.info("Activated invite link: {}", link);
    }

    @Override
    @Transactional
    public void deactivateLink(String link) {
        LOG.debug("Deactivating invite link: {}", link);
        InviteLink inviteLink = inviteLinkRepository.findByLink(link)
                .orElseThrow(() -> new EntityNotFoundException("Invite link not found: " + link));
        inviteLink.setActive(false);
        inviteLinkRepository.save(inviteLink);
        LOG.info("Deactivated invite link: {}", link);
    }

    @Override
    @Transactional
    public boolean useInviteLink(String link, Long userId) {
        LOG.debug("User {} is attempting to use invite link: {}", userId, link);
        InviteLink inviteLink = inviteLinkRepository.findByLink(link)
                .filter(InviteLink::isActive)
                .orElseThrow(() -> new EntityNotFoundException("Active invite link not found: " + link));

        if (inviteLink.getExpiryDate().isAfter(LocalDateTime.now())) {
            dashboardAccessService.grantAccess(userId, inviteLink.getDashboard().getId(), EAccessLevel.VIEWER);
            LOG.info("Granted access to user {} for dashboard ID {} via invite link {}", userId, inviteLink.getDashboard().getId(), link);
            dashboardRoleService.assignRoleToUserInDashboard(userId, inviteLink.getDashboard().getId(), ERole.NONE);
            LOG.info("Assigned role to user {} for dashboard ID {} via invite link {}", userId, inviteLink.getDashboard().getId(), link);
            return true;
        } else {
            LOG.warn("Attempt to use expired link: {}", link);
            throw new IllegalStateException("Link is expired");
        }
    }
}