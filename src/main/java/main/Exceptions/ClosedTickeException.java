package main.Exceptions;

public class ClosedTickeException extends RuntimeException {
    public ClosedTickeException(String message) {
        super(message);
    }
}
