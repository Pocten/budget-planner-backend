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
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleNames")
    UserDto toDto(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "userEmail", target = "userEmail")
    @Mapping(source = "userPassword", target = "userPassword")
    @Mapping(source = "userDateRegistration", target = "userDateRegistration")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "roleNamesToRoles")
    User toEntity(UserDto dto);

    @Named("rolesToRoleNames")
    default Set<String> rolesToRoleNames(Set<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    @Named("roleNamesToRoles")
    default Set<Role> roleNamesToRoles(Set<String> roleNames) {
        if (roleNames == null) {
            return null;
        }

        return roleNames.stream()
                .map(name -> new Role(null, ERole.valueOf(name)))
                .collect(Collectors.toSet());
    }
}
