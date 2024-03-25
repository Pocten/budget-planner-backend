package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.DashboardDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "dateCreated", target = "dateCreated")
    @Mapping(source = "user.id", target = "userId")
    DashboardDto toDto(Dashboard dashboard);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "dateCreated", target = "dateCreated")
    @Mapping(source = "userId", target = "user.id")
    Dashboard toEntity(DashboardDto dashboardDto);
}
