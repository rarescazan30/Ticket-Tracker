package main.Exceptions;

public class TicketAlreadyAssignedException extends RuntimeException {
    public TicketAlreadyAssignedException(String message) {
        super(message);
    }
}
