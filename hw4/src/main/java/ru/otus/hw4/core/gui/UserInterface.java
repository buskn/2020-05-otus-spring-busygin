package ru.otus.hw4.core.gui;

import ru.otus.hw4.core.question.Answer;
import ru.otus.hw4.core.question.Question;

public interface UserInterface {
    void intro();
    void greet(String username);
    void showQuestion(Question question, int number);
    void showAnswer(Answer answer, int number);
    String receiveFreeFormResponse();
    int receiveTestFormResponse();
    void showTestAnswerBounds(int minNum, int maxNum);
    void congratulate(String username);
    void console(String username);
    void showRatio(double ratio);
}
