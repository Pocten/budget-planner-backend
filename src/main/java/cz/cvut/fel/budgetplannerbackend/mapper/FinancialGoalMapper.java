package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialGoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = DashboardMapper.class)
public interface FinancialGoalMapper {

    @Mapping(source = "dashboard.id", target = "dashboardId")
    FinancialGoalDto toDto(FinancialGoal financialGoal);

    @Mapping(source = "dashboardId", target = "dashboard.id")
    FinancialGoal toEntity(FinancialGoalDto financialGoalDto);

}