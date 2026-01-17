package main.Exceptions;

/**
 * Exception thrown when a developer lacks the required expertise for a ticket
 */
public class InvalidExpertiseException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public InvalidExpertiseException(final String message) {
        super(message);
    }
}
