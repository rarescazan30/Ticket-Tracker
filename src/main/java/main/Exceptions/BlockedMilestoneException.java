package main.Exceptions;

/**
 * Exception thrown when an operation is attempted on a blocked milestone
 */
public class BlockedMilestoneException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public BlockedMilestoneException(final String message) {
        super(message);
    }
}
