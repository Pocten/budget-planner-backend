package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.BudgetDto;
import cz.cvut.fel.budgetplannerbackend.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DashboardMapper.class})
public interface BudgetMapper {

    @Mapping(source = "dashboard", target = "dashboard")
    BudgetDto toDto(Budget budget);

    @Mapping(target = "dashboard", ignore = true) // I'll set it manually in the service layer
    Budget toEntity(BudgetDto budgetDto);
}
