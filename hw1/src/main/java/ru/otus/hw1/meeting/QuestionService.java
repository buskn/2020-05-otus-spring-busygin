package ru.otus.hw1.meeting;

import java.util.List;

/**
 * Сервис для получения блоков вопросов
 */
public interface QuestionService {
    List<QuestionBlock> getAllQuestionBlocks();
}
