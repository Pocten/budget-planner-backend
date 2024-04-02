package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.FinancialRecordDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.FinancialRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DashboardMapper.class, CategoryMapper.class, UserMapper.class, TagMapper.class, UserMapper.class})
public interface FinancialRecordMapper {

    @Mapping(source = "dashboard", target = "dashboard")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "tags", target = "tags")
    FinancialRecordDto toDto(FinancialRecord financialRecord);

    @Mapping(target = "dashboard", source = "dashboard")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", ignore = true) // Игнорируем теги при создании сущности из DTO
    FinancialRecord toEntity(FinancialRecordDto financialRecordDto);

    default Dashboard dashboardFromId(Long id) {
        if (id == null) {
            return null;
        }
        Dashboard dashboard = new Dashboard();
        dashboard.setId(id);
        return dashboard;
    }

    default Category categoryFromId(Long id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
