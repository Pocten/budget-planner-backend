package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = DashboardMapper.class)
public interface CategoryMapper {

    @Mapping(source = "dashboard", target = "dashboard")
    CategoryDto toDto(Category category);

    @Mapping(target = "dashboard", ignore = true) // I'll set it manually in the service layer
    Category toEntity(CategoryDto categoryDto);
}
