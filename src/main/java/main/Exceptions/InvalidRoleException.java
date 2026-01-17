package main.Exceptions;

/**
 * Exception thrown when a user role is not authorized for a specific action
 */
public class InvalidRoleException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public InvalidRoleException(final String message) {
        super(message);
    }
}
