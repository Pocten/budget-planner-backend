package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.CategoryDto;
import cz.cvut.fel.budgetplannerbackend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = DashboardMapper.class)
public interface CategoryMapper {

    @Mapping(source = "dashboard", target = "dashboard")
    CategoryDto toDto(Category category);

    @Mapping(target = "dashboard", ignore = true) // Ignore it because you need to install it manually
    Category toEntity(CategoryDto categoryDto);
}
