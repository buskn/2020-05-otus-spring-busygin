package ru.otus.hw4.core.question;

import lombok.Data;

import java.util.List;


/**
 * Блок "вопрос-ответы"
 */
@Data
public class QuestionBlock {
    private final Question question;
    private final List<Answer> answers;
    private final boolean freeForm;
}
