package cz.cvut.fel.budgetplannerbackend.exceptions.role;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super("Role not found");
    }
}
