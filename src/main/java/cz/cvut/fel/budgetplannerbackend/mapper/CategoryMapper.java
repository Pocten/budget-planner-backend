package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "dashboard.id", target = "dashboardId")
    CategoryDto toDto(Category category);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "dashboardId", target = "dashboard.id")
    Category toEntity(CategoryDto categoryDto);
}
