package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardRoleDto;
import cz.cvut.fel.budgetplannerbackend.entity.DashboardRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DashboardRoleMapper {

    DashboardRoleDto toDto(DashboardRole dashboardRole);

    DashboardRole toEntity(DashboardRoleDto dto);
}