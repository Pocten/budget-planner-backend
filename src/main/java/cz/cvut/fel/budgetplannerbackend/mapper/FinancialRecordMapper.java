package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinancialRecordMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "dashboard.id", target = "dashboardId")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "description", target = "description")
    FinancialRecordDto toDto(FinancialRecord financialRecord);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "dashboardId", target = "dashboard.id")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "description", target = "description")
    FinancialRecord toEntity(FinancialRecordDto financialRecordDto);
}
