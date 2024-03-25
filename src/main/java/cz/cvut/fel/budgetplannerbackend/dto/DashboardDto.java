package cz.cvut.fel.budgetplannerbackend.dto;

import java.time.LocalDateTime;

public record DashboardDto(
        Long id,
        String title,
        String description,
        LocalDateTime dateCreated,
        Long userId
) {
}
