package ru.otus.hw2.utils.csv.exception;

import ru.otus.hw2.core.exception.Hw2Exception;

public class MalformedCSVException extends Hw2Exception {
    public MalformedCSVException(String msg) {
        super(msg);
    }
}
