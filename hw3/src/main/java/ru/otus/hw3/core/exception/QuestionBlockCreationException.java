package ru.otus.hw3.core.exception;

public class QuestionBlockCreationException extends HwException {
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
