package main.Exceptions;

/**
 * Exception thrown when the user reporting a ticket does not have permission
 */
public class InvalidReporterException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public InvalidReporterException(final String message) {
        super(message);
    }
}
