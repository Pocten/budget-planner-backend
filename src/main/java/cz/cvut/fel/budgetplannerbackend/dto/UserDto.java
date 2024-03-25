package cz.cvut.fel.budgetplannerbackend.dto;


import java.time.LocalDateTime;
import java.util.Set;

public record UserDto(
        Long id,
        String userName,
        String userEmail,
        String userPassword,
        LocalDateTime userDateRegistration,
        Set<String>roles
) { }
