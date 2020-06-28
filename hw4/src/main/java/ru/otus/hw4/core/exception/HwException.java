package ru.otus.hw4.core.exception;

/**
 * Маркерное исключение текущего проекта
 */
public class HwException extends RuntimeException {
    public HwException() {
    }

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
