package main.Exceptions;

public class InvalidExpertiseException extends RuntimeException {
    public InvalidExpertiseException(String message) {
        super(message);
    }
}
