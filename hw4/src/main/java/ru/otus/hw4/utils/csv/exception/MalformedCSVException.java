package ru.otus.hw4.utils.csv.exception;

import ru.otus.hw4.core.exception.HwException;

public class MalformedCSVException extends HwException {
    public MalformedCSVException(String msg) {
        super(msg);
    }
}
