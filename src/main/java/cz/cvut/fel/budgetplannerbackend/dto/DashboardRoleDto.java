package cz.cvut.fel.budgetplannerbackend.dto;


public record DashboardRoleDto(
        Long id,
        UserDto user,
        DashboardDto dashboard,
        RoleDto role
) {}
