package cz.cvut.fel.budgetplannerbackend.mapper;

import cz.cvut.fel.budgetplannerbackend.dto.UserDTO;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);

    default User toEntityWithId(UserDTO userDTO, Long id) {
        User user = toEntity(userDTO);
        user.setId(id);
        return user;
    }
}
