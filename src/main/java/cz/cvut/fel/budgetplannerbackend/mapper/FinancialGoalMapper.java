package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialGoalDto;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialGoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BudgetMapper.class)
public interface FinancialGoalMapper {

    @Mapping(source = "budget.id", target = "budgetId")
    FinancialGoalDto toDto(FinancialGoal financialGoal);

    @Mapping(source = "budgetId", target = "budget.id")
    FinancialGoal toEntity(FinancialGoalDto financialGoalDto);
}

