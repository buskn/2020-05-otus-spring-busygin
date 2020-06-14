package ru.otus.hw2.utils.exception;

import ru.otus.hw2.core.exception.Hw2Exception;

public class WrapExceptionsAspectException extends Hw2Exception {
    public WrapExceptionsAspectException(String message) {
        super(message);
    }

    public WrapExceptionsAspectException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrapExceptionsAspectException(Throwable cause) {
        super(cause);
    }
}
