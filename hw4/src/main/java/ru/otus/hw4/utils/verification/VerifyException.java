package ru.otus.hw4.utils.verification;

import ru.otus.hw4.core.exception.HwException;

public class VerifyException extends HwException {
    public VerifyException(String message) {
        super(message);
    }

    public VerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerifyException(Throwable cause) {
        super(cause);
    }
}
