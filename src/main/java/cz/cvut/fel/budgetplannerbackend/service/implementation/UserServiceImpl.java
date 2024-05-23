package cz.cvut.fel.budgetplannerbackend.service.implementation;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.entity.Dashboard;
import cz.cvut.fel.budgetplannerbackend.entity.User;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityAlreadyExistsException;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.mapper.UserMapper;
import cz.cvut.fel.budgetplannerbackend.repository.DashboardRepository;
import cz.cvut.fel.budgetplannerbackend.repository.UserRepository;
import cz.cvut.fel.budgetplannerbackend.service.DashboardService;
import cz.cvut.fel.budgetplannerbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;
    private final DashboardService dashboardService;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        LOG.info("Getting all users");
        List<User> users = userRepository.findAll();
        LOG.info("Returned all users");
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        LOG.info("Getting user with id: {}", id);
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User", id));
            LOG.info("Returned user with id: {}", id);
            return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) throws EntityAlreadyExistsException {
        LOG.info("Creating user");
        User user = userMapper.toEntity(userDto);

        if (userRepository.findUserByUserName(user.getUserName()).isPresent() || userRepository.findUserByUserEmail(user.getUserEmail()).isPresent()) {
            LOG.warn("User already exists");
            throw new EntityAlreadyExistsException("User", user.getUserName());
        }

        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        User savedUser = userRepository.save(user);
        LOG.info("Created user");
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        LOG.info("Updating user with id: {}", id);
        return userRepository.findById(id).map(existingUser -> {
            if (userDto.userEmail() != null) {
                existingUser.setUserEmail(userDto.userEmail());
            }
            if (userDto.userPassword() != null) {
                existingUser.setUserPassword(passwordEncoder.encode(userDto.userPassword()));
            }

            User updatedUser = userRepository.save(existingUser);
            LOG.info("Updated user with id: {}", id);
            return userMapper.toDto(updatedUser);
        }).orElseThrow(() -> {
            LOG.warn("User with id {} not found", id);
            return new EntityNotFoundException("User", id);
        });
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {
        LOG.info("Deleting user with id: {}", id);
        if (userRepository.existsById(id)) {
            // Найдем все дашборды пользователя
            List<Dashboard> userDashboards = dashboardRepository.findAllByUserId(id);

            // Удалим все дашборды пользователя через существующий метод deleteDashboard
            for (Dashboard dashboard : userDashboards) {
                dashboardService.deleteDashboard(id, dashboard.getId());
            }

            // Удаление самого пользователя
            userRepository.deleteById(id);
            LOG.info("Deleted user with id: {}", id);
        } else {
            LOG.warn("User with id {} not found", id);
            throw new EntityNotFoundException("User", id);
        }
    }
}
