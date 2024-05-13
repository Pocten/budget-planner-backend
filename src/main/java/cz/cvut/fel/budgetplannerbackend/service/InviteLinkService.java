package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.InviteLinkDto;


public interface InviteLinkService {

    InviteLinkDto createInviteLink(InviteLinkDto inviteLinkDto);

    void checkAndRefreshLinks();

    void activateLink(String link);

    void deactivateLink(String link);

    boolean useInviteLink(String link, Long userId);
}


