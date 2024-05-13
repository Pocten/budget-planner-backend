package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DashboardMapper.class, CategoryMapper.class, UserMapper.class, TagMapper.class})
public interface FinancialRecordMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "dashboard", target = "dashboard")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "tags", target = "tags")
    FinancialRecordDto toDto(FinancialRecord financialRecord);

    @Mapping(source = "userId", target = "user")
    @Mapping(target = "dashboard", source = "dashboard")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", ignore = true) // Игнорируем теги при создании сущности из DTO
    FinancialRecord toEntity(FinancialRecordDto financialRecordDto);

}
