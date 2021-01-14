package ru.otus.hw6;

public class HwException extends RuntimeException {
    public HwException(String message) {
        super(message);
    }

    public HwException(String message, Throwable cause) {
        super(message, cause);
    }

    public HwException(Throwable cause) {
        super(cause);
    }
}
