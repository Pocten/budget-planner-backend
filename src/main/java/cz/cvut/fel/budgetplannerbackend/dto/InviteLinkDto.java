package cz.cvut.fel.budgetplannerbackend.dto;

import java.time.LocalDateTime;

public record InviteLinkDto(
        Long id,
        String link,
        LocalDateTime expiryDate,
        boolean active,
        Long dashboardId
) {}