package ru.otus.hw1.service.exception;

/**
 * Рантайм-аналог для QuestionBlockCreationException
 */
public class QBCRuntimeException extends RuntimeException {
    public QBCRuntimeException(String msg) {
        super(msg);
    }
    public QBCRuntimeException(Throwable cause) {
        super(cause);
    }
}
