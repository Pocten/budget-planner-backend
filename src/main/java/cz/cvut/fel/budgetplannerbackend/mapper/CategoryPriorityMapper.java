package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryPriorityDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import cz.cvut.fel.budgetplannerbackend.entity.CategoryPriority;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class, DashboardMapper.class})
public interface CategoryPriorityMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "dashboard.id", target = "dashboardId")
    CategoryPriorityDto toDto(CategoryPriority categoryPriority);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "dashboard", source = "dashboardId")
    CategoryPriority toEntity(CategoryPriorityDto categoryPriorityDto);

    default Category categoryFromId(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    default Dashboard dashboardFromId(Long dashboardId) {
        if (dashboardId == null) {
            return null;
        }
        Dashboard dashboard = new Dashboard();
        dashboard.setId(dashboardId);
        return dashboard;
    }
}