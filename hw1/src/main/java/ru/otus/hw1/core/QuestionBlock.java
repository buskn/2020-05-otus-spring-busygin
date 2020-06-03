package ru.otus.hw1.core;

import java.util.List;

/**
 * Блок, содержащий вопрос и возможные вырианты ответа на него
 */
public interface QuestionBlock {
    /**
     * @return Вопрос
     */
    Question getQuestion();

    /**
     * @return Предполагается ли отвечать на вопрос в свободной форме, без вариантов ответов
     */
    boolean isFreeFormAnswer();

    /**
     * @return Варианты ответа на вопрос
     */
    List<Answer> getAnswers();
}
