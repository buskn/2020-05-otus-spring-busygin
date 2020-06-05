package ru.otus.hw1.core;

/**
 * Ответ на вопрос в некоторой форме
 */
public interface Answer extends Material {
    /**
     * @return Является ли данный ответ правильным
     */
    boolean isCorrect();
}
