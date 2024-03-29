package cz.cvut.fel.budgetplannerbackend.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String entityName, Object keyValue) {
        super(String.format("%s with id %s already exists", entityName, keyValue));
    }
}
