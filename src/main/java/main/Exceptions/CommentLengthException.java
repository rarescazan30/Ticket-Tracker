package main.Exceptions;

/**
 * Exception thrown when a comment exceeds the maximum allowed length
 */
public class CommentLengthException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public CommentLengthException(final String message) {
        super(message);
    }
}
