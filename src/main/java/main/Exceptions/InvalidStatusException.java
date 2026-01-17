package main.Exceptions;

/**
 * Exception thrown when an invalid status transition or value is encountered
 */
public class InvalidStatusException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public InvalidStatusException(final String message) {
        super(message);
    }
}
