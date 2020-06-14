package ru.otus.hw2.core.question;

import lombok.Data;

/**
 * Реализация ответа
 */
@Data
public class Answer {
    private final String text;
    private final boolean correct;
}
