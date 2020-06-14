package ru.otus.hw2.core.gui;

import ru.otus.hw2.core.exception.UserInterfaceException;
import ru.otus.hw2.core.question.Answer;
import ru.otus.hw2.core.question.Question;

public interface UserInterface {
    void showQuestion(Question question, int number) throws UserInterfaceException;
    void showAnswer(Answer answer, int number) throws UserInterfaceException;
    String receiveFreeFormResponse() throws UserInterfaceException;
    int receiveTestFormResponse() throws UserInterfaceException;
    public void showMessage(String message) throws UserInterfaceException;
    public void showSeparator() throws UserInterfaceException;
}
