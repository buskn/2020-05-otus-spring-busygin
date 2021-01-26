package ru.otus.hw6.common;

import lombok.Getter;

public class HwException extends RuntimeException {
    @Getter
    private Object[] params = new Object[0];

    public HwException(String message, Object ... params) {
        super(message);
        if (params != null)
            this.params = params;
    }

    public HwException(String message, Throwable cause) {
        super(message, cause);
    }

    public HwException(Throwable cause) {
        super(cause);
    }
}
