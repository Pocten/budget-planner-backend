package cz.cvut.fel.budgetplannerbackend.dto;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String userName,
        String userEmail,
        LocalDateTime userDateRegistration
) { }
