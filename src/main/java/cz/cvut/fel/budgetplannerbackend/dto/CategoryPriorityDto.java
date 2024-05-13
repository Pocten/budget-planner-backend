package cz.cvut.fel.budgetplannerbackend.dto;

public record CategoryPriorityDto(
        Long id,
        Long userId,
        Long categoryId,
        Long dashboardId,
        Integer priority
) {
}
