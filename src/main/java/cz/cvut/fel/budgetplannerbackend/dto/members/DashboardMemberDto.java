package cz.cvut.fel.budgetplannerbackend.dto.members;


public record DashboardMemberDto(
        Long userId,
        String userName,
        String userEmail,
        String accessLevel,
        String role
) {}
