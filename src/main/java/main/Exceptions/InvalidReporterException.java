package main.Exceptions;

public class InvalidReporterException extends RuntimeException {
    public InvalidReporterException(String message) {
        super(message);
    }
}
