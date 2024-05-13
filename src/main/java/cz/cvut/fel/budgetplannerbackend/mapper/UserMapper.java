package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.entity.Role;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "userEmail", target = "userEmail")
    @Mapping(source = "userPassword", target = "userPassword")
    @Mapping(source = "userDateRegistration", target = "userDateRegistration")
    UserDto toDto(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "userEmail", target = "userEmail")
    @Mapping(source = "userPassword", target = "userPassword")
    @Mapping(source = "userDateRegistration", target = "userDateRegistration")
    User toEntity(UserDto dto);

}
