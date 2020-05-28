package ru.otus.hw1.meeting.exception;

public class QuestionBlockCreationException extends Exception {
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
