package cz.cvut.fel.budgetplannerbackend.dto;

import cz.cvut.fel.budgetplannerbackend.entity.enums.EAccessLevel;

public record AccessLevelDto(
        Long id,
        EAccessLevel level
) {}
