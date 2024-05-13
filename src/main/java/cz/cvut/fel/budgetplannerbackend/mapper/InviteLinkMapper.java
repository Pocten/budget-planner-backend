package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.InviteLinkDto;
import cz.cvut.fel.budgetplannerbackend.entity.InviteLink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InviteLinkMapper {

    @Mapping(source = "dashboard.id", target = "dashboardId")
    InviteLinkDto toDto(InviteLink inviteLink);

    @Mapping(source = "dashboardId", target = "dashboard.id")
    InviteLink toEntity(InviteLinkDto dto);
}



