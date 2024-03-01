package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "userEmail", target = "userEmail")
    @Mapping(source = "userPassword", target = "userPassword")
    @Mapping(source = "userDateRegistration", target = "userDateRegistration")
    UserDto toDto(User user);

    @InheritInverseConfiguration
    User toEntity(UserDto dto);
}
