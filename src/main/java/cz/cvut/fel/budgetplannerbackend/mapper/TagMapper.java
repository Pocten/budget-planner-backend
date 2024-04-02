package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.TagDto;
import cz.cvut.fel.budgetplannerbackend.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DashboardMapper.class})
public interface TagMapper {
    @Mapping(source = "dashboard", target = "dashboard")
    TagDto toDto(Tag tag);

    @Mapping(target = "dashboard", ignore = true) // I'll set it manually in the service layer
    Tag toEntity(TagDto tagDto);
}
