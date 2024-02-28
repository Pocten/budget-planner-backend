package cz.cvut.fel.budgetplannerbackend.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String userName,
        String userEmail,
        String userPassword,
        LocalDateTime userDateRegistration
) { }
