package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardAccessDto;
import cz.cvut.fel.budgetplannerbackend.entity.DashboardAccess;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DashboardAccessMapper {

    DashboardAccessDto toDto(DashboardAccess entity);

    DashboardAccess toEntity(DashboardAccessDto dto);

}