package ru.otus.hw4.core.credentials;

import ru.otus.hw4.core.exception.HwException;

public class CredentialsException extends HwException {
    public CredentialsException(String message) {
        super(message);
    }

    public CredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialsException(Throwable cause) {
        super(cause);
    }
}