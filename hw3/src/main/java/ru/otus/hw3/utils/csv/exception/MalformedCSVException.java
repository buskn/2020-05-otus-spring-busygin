package ru.otus.hw3.utils.csv.exception;

import ru.otus.hw3.core.exception.HwException;

public class MalformedCSVException extends HwException {
    public MalformedCSVException(String msg) {
        super(msg);
    }
}
