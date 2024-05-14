package cz.cvut.fel.budgetplannerbackend.dto;

import cz.cvut.fel.budgetplannerbackend.entity.enums.ERecordType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record FinancialRecordDto(
        Long id,
        Long userId,
        DashboardDto dashboard,
        BigDecimal amount,
        CategoryDto category, // Can be null
        ERecordType type,
        LocalDateTime date,
        String description // Can be null
) {
}
