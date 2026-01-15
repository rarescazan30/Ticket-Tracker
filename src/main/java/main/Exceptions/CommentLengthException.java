package main.Exceptions;

public class CommentLengthException extends RuntimeException {
    public CommentLengthException(String message) {
        super(message);
    }
}
