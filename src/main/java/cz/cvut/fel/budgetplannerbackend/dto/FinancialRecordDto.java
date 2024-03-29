package cz.cvut.fel.budgetplannerbackend.dto;

import cz.cvut.fel.budgetplannerbackend.entity.enums.ERecordType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinancialRecordDto(
        Long id,
        Long dashboardId,
        BigDecimal amount,
        Long categoryId,
        ERecordType type,
        LocalDateTime date,
        String description
) {
}
