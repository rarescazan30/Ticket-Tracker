package main.Exceptions;

public class InvalidMilestoneAccessException extends RuntimeException {
    public InvalidMilestoneAccessException(String message) {
        super(message);
    }
}
