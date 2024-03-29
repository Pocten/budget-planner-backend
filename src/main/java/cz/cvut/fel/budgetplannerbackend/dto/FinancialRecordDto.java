package cz.cvut.fel.budgetplannerbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinancialRecordDto(
        Long id,
        Long dashboardId,
        BigDecimal amount,
        Long categoryId,
        LocalDateTime date,
        String description
) {
}
