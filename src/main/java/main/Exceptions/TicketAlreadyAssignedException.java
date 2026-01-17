package main.Exceptions;

/**
 * Exception thrown when attempting to assign a ticket that is already assigned
 */
public class TicketAlreadyAssignedException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public TicketAlreadyAssignedException(final String message) {
        super(message);
    }
}
