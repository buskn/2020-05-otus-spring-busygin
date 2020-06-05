package ru.otus.hw1.service;

import ru.otus.hw1.core.Answer;

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
