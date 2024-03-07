package cz.cvut.fel.budgetplannerbackend.service;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.entity.Role;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.entity.enums.ERole;
import cz.cvut.fel.budgetplannerbackend.exceptions.UserAlreadyExistsException;
import cz.cvut.fel.budgetplannerbackend.exceptions.UserNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.UserMapper;
import cz.cvut.fel.budgetplannerbackend.repository.RoleRepository;
import cz.cvut.fel.budgetplannerbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) throws UserAlreadyExistsException {
        User user = userMapper.toEntity(userDto);

        if (userRepository.findUserByUserName(user.getUserName()).isPresent() || userRepository.findUserByUserEmail(user.getUserEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        Set<String> strRoles = userDto.roles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        if (userRepository.existsById(id)) {
            User user = userMapper.toEntity(userDto);
            user.setId(id);
            User updatedUser = userRepository.save(user);
            return userMapper.toDto(updatedUser);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }
}
