package cz.cvut.fel.budgetplannerbackend.dto;

import java.time.LocalDate;

public record BudgetDto(
        Long id,
        DashboardDto dashboard,
        String title,
        Double totalAmount,
        LocalDate startDate,
        LocalDate endDate
) {
}
