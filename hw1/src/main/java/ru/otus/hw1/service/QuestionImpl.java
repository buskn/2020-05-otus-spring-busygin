package ru.otus.hw1.service;

import ru.otus.hw1.core.Question;

/**
 * Реализация вопроса
 */
public class QuestionImpl implements Question {
    private final String text;

    public QuestionImpl(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
