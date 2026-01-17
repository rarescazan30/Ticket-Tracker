package main.Exceptions;

/**
 * Exception thrown when an invalid time period is provided for an operation
 */
public class InvalidPeriodException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public InvalidPeriodException(final String message) {
        super(message);
    }
}
