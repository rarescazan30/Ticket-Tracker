package main.Exceptions;

/**
 * Exception thrown when a user tries to access a milestone they are not linked to
 */
public class InvalidMilestoneAccessException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public InvalidMilestoneAccessException(final String message) {
        super(message);
    }
}
