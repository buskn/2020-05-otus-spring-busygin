package ru.otus.hw3.core.question;

import ru.otus.hw3.core.exception.QuestionBlockCreationException;

import java.util.List;

/**
 * Интерфейс доступа к вопросам и ответам
 */
public interface QuestionDao {
    /**
     * @return список всех доступных блоков вопросов
     * @throws QuestionBlockCreationException при ошибке получения блоков вопросов
     */
    public List<QuestionBlock> getAllQuestionBlocks() throws QuestionBlockCreationException;
}
