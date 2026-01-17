package main.Exceptions;

/**
 * Exception thrown when an anonymous user attempts to report a non-bug ticket
 */
public class NonBugAnonymous extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message
     */
    public NonBugAnonymous(final String message) {
        super(message);
    }
}
