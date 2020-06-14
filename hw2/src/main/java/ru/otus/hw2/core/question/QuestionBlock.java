package ru.otus.hw2.core.question;

import lombok.Data;
import java.util.List;


/**
 * Реализация блока "вопрос-ответы"
 */
@Data
public class QuestionBlock {
    private final Question question;
    private final List<Answer> answers;
    private final boolean freeForm;
}
