package cz.cvut.fel.budgetplannerbackend.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Object keyValue) {
        super(String.format("%s with id %s not found", entityName, keyValue));
    }
}
