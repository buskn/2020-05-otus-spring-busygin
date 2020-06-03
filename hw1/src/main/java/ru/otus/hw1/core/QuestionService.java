package ru.otus.hw1.core;

import ru.otus.hw1.core.exception.QuestionBlockCreationException;

import java.util.List;

/**
 * Сервис для получения блоков вопросов
 */
public interface QuestionService {
    List<QuestionBlock> getAllQuestionBlocks() throws QuestionBlockCreationException;
}
