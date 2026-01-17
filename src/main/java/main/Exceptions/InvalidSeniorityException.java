package main.Exceptions;

/**
 * Exception thrown when a developer's seniority is insufficient for a ticket
 */
public class InvalidSeniorityException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public InvalidSeniorityException(final String message) {
        super(message);
    }
}
