package ru.otus.hw6.common;

public class HwDevelopException extends HwException {
    public HwDevelopException(String message, Object... params) {
        super(message, params);
    }

    public HwDevelopException(String message, Throwable cause) {
        super(message, cause);
    }

    public HwDevelopException(Throwable cause) {
        super(cause);
    }
}
