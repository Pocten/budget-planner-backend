package cz.cvut.fel.budgetplannerbackend.dto.members;

public record MemberRequestDto(
        // Username or email of the user to be added.
        String usernameOrEmail) {}

