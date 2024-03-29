package cz.cvut.fel.budgetplannerbackend.dto;

public record CategoryDto(
        Long id,
        String name,
        String description,
        Long dashboardId
) {
}
