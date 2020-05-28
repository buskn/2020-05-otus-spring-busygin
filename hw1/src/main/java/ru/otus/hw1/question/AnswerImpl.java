package ru.otus.hw1.question;

import ru.otus.hw1.meeting.Answer;

/**
 * Реализация ответа
 */
public class AnswerImpl implements Answer {
    private final String text;
    private final boolean correct;

    public AnswerImpl(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    @Override
    public boolean isCorrect() {
        return correct;
    }

    @Override
    public String getText() {
        return text;
    }
}
