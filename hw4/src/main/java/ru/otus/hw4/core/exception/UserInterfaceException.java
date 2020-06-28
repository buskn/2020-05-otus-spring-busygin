package ru.otus.hw4.core.exception;

public class UserInterfaceException extends HwException {
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
