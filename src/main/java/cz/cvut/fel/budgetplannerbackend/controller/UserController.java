package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.EntityNotFoundException;
import cz.cvut.fel.budgetplannerbackend.service.implementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users.
 * This controller provides endpoints for retrieving, creating, updating, and deleting users.
 */
@RestController
@RequestMapping("/api/v1/users") // Base URL for all user-related endpoints.
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService; // Service class for handling user operations.
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    /**
     * Retrieves a list of all users.
     *
     * @return A ResponseEntity containing a list of UserDto objects and an HTTP status of 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        LOG.info("Received request to get all users");
        List<UserDto> userDtos = userService.getAllUsers(); // Retrieve all users from the service.
        LOG.info("Returned all users");
        return ResponseEntity.ok(userDtos); // Return the list of users with an OK status.
    }

    /**
     * Retrieves a specific user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A ResponseEntity containing the UserDto object and an HTTP status of 200 OK if found,
     *         or a 404 Not Found status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        LOG.info("Received request to get user with id: {}", id);
        try {
            UserDto userDto = userService.getUserById(id); // Retrieve the user from the service.
            LOG.info("Returned user with id: {}", id);
            return ResponseEntity.ok(userDto); // Return the user with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error getting user", e); // Log the exception if the user is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Creates a new user.
     *
     * @param userDto The UserDto object containing user data.
     * @return A ResponseEntity containing the created UserDto object and an HTTP status of 201 Created.
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        LOG.info("Received request to create user");
        UserDto createdUserDto = userService.createUser(userDto); // Create the user using the service.
        LOG.info("Created user");
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED); // Return the created user with a Created status.
    }

    /**
     * Updates an existing user.
     *
     * @param id       The ID of the user to update.
     * @param userDto The UserDto object containing the updated user data.
     * @return A ResponseEntity containing the updated UserDto object and an HTTP status of 200 OK if found,
     *         or a 404 Not Found status if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        LOG.info("Received request to update user with id: {}", id);
        try {
            UserDto updatedUserDto = userService.updateUser(id, userDto); // Update the user using the service.
            LOG.info("Updated user with id: {}", id);
            return ResponseEntity.ok(updatedUserDto); // Return the updated user with an OK status.
        } catch (EntityNotFoundException e) {
            LOG.error("Error updating user", e); // Log the exception if the user is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }

    /**
     * Deletes a user.
     *
     * @param id The ID of the user to delete.
     * @return A ResponseEntity with an HTTP status of 204 No Content if successful,
     *         or a 404 Not Found status if the user is not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        LOG.info("Received request to delete user with id: {}", id);
        try {
            userService.deleteUser(id); // Delete the user using the service.
            LOG.info("Deleted user with id: {}", id);
            return ResponseEntity.noContent().build(); // Return a No Content status to indicate successful deletion.
        } catch (EntityNotFoundException e) {
            LOG.error("Error deleting user", e); // Log the exception if the user is not found.
            return ResponseEntity.notFound().build(); // Return a Not Found status.
        }
    }
}