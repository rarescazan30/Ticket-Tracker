package main.Exceptions;

/**
 * Exception thrown when an operation is attempted on an already closed ticket
 */
public class ClosedTickeException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public ClosedTickeException(final String message) {
        super(message);
    }
}
