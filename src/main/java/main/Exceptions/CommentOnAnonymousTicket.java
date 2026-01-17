package main.Exceptions;

/**
 * Exception thrown when a comment is attempted on a ticket with an anonymous reporter
 */
public class CommentOnAnonymousTicket extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public CommentOnAnonymousTicket(final String message) {
        super(message);
    }
}
