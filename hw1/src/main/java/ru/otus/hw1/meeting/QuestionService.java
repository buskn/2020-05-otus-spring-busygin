package ru.otus.hw1.meeting;

import ru.otus.hw1.meeting.exception.QuestionBlockCreationException;

import java.util.List;

/**
 * Сервис для получения блоков вопросов
 */
public interface QuestionService {
    List<QuestionBlock> getAllQuestionBlocks() throws QuestionBlockCreationException;
}
