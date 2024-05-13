package cz.cvut.fel.budgetplannerbackend.dto;

import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;

public record RoleDto(
        Long id,
        ERole role
) {
}
