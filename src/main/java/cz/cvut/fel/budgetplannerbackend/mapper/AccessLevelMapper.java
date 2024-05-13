package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.AccessLevelDto;
import cz.cvut.fel.budgetplannerbackend.entity.AccessLevel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccessLevelMapper {

    AccessLevelDto toDto(AccessLevel accessLevel);

    AccessLevel toEntity(AccessLevelDto accessLevelDto);
}
