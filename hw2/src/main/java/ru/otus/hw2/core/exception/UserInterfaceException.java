package ru.otus.hw2.core.exception;

public class UserInterfaceException extends Hw2Exception {
    public UserInterfaceException() {
    }

    public UserInterfaceException(String message) {
        super(message);
    }

    public UserInterfaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserInterfaceException(Throwable cause) {
        super(cause);
    }
}
