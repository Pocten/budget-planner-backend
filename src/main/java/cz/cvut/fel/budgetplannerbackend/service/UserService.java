package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.user.UserAlreadyExistsException;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createUser(UserDto userDto) throws UserAlreadyExistsException;

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
