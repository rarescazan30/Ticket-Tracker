package main.Exceptions;

public class CommentOnAnonymousTicket extends RuntimeException {
    public CommentOnAnonymousTicket(String message) {
        super(message);
    }
}
