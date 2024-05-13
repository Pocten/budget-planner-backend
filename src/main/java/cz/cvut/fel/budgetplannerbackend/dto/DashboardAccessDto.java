package cz.cvut.fel.budgetplannerbackend.dto;

public record DashboardAccessDto(
        Long id,
        UserDto user,
        DashboardDto dashboard,
        AccessLevelDto accessLevel
) {
}
