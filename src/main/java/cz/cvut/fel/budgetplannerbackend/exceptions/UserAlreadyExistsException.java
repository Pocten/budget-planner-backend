package cz.cvut.fel.budgetplannerbackend.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("User with this username or email already exists.");
    }

}
