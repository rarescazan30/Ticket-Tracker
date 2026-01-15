package main.Exceptions;

public class InvalidTicketAssignmentException extends RuntimeException {
    public InvalidTicketAssignmentException(String message) {
        super(message);
    }
}
