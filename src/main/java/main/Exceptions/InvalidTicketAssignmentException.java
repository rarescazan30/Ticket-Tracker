package main.Exceptions;

/**
 * Exception thrown when a ticket is assigned to an incompatible developer
 */
public class InvalidTicketAssignmentException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public InvalidTicketAssignmentException(final String message) {
        super(message);
    }
}
