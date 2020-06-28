package ru.otus.hw3.core.question;

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
