package ru.otus.hw4.core.question;

import lombok.Value;

import java.util.List;


/**
 * Блок "вопрос-ответы"
 */
@Value
public class QuestionBlock {
    Question question;
    List<Answer> answers;
    boolean freeForm;
}
