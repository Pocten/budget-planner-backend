package cz.cvut.fel.budgetplannerbackend.dto;

public record TagDto(
        Long id,
        String name,
        String description,
        DashboardDto dashboard
) {
}
