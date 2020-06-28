package ru.otus.hw3.core.question;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw3.core.exception.QuestionBlockCreationException;

import java.util.List;

/**
 * Реализация сервиса вопросов
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {
    private final QuestionDao dao;

    private List<QuestionBlock> blocks;

    /**
     * @return Список всех полученных блоков вопросов
     * @throws QuestionBlockCreationException при ошибке конструирования блоков из источника
     */
    public List<QuestionBlock> getAllQuestionBlocks() throws QuestionBlockCreationException {
        if (blocks == null)
            blocks = dao.getAllQuestionBlocks();
        return blocks;
    }

}
