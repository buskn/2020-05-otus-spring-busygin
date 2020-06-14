package ru.otus.hw2.core.exception;

/**
 * Маркерное исключение текущего проекта
 */
public class Hw2Exception extends RuntimeException {
    public Hw2Exception() {
    }

    public Hw2Exception(String message) {
        super(message);
    }

    public Hw2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Hw2Exception(Throwable cause) {
        super(cause);
    }
}
