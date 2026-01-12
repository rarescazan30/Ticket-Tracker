package main.Exceptions;

public class BlockedMilestoneException extends RuntimeException {
    public BlockedMilestoneException(String message) {
        super(message);
    }
}
