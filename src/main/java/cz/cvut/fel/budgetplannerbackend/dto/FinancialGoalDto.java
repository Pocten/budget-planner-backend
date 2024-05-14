package cz.cvut.fel.budgetplannerbackend.dto;

import java.time.LocalDate;

public record FinancialGoalDto(
        Long id,
        Long dashboardId,
        String title,
        Double targetAmount,
        Double currentAmount,
        LocalDate deadline
) {}


