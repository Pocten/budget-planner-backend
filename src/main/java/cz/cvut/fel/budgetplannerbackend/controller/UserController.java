package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.user.UserNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.implementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        LOG.info("Received request to get all users");
        List<UserDto> userDtos = userService.getAllUsers();
        LOG.info("Returned all users");
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        LOG.info("Received request to get user with id: {}", id);
        try {
            UserDto userDto = userService.getUserById(id);
            LOG.info("Returned user with id: {}", id);
            return ResponseEntity.ok(userDto);
        } catch (UserNotFoundException e) {
            LOG.error("Error getting user", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        LOG.info("Received request to create user");
        UserDto createdUserDto = userService.createUser(userDto);
        LOG.info("Created user");
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        LOG.info("Received request to update user with id: {}", id);
        try {
            UserDto updatedUserDto = userService.updateUser(id, userDto);
            LOG.info("Updated user with id: {}", id);
            return ResponseEntity.ok(updatedUserDto);
        } catch (UserNotFoundException e) {
            LOG.error("Error updating user", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        LOG.info("Received request to delete user with id: {}", id);
        try {
            userService.deleteUser(id);
            LOG.info("Deleted user with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            LOG.error("Error deleting user", e);
            return ResponseEntity.notFound().build();
        }
    }
}