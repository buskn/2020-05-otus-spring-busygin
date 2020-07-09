package ru.otus.hw5.ui;

import ru.otus.hw5.HwException;

public class CommandShellException extends HwException {
    public CommandShellException(String message) {
        super(message);
    }

    public CommandShellException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandShellException(Throwable cause) {
        super(cause);
    }
}
