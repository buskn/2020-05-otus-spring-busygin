package ru.otus.hw2.core.exception;

public class QuestionBlockCreationException extends Hw2Exception {
    public QuestionBlockCreationException(String msg) {
        super(msg);
    }
    public QuestionBlockCreationException(Throwable e) {
        super(e);
    }
    public QuestionBlockCreationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
