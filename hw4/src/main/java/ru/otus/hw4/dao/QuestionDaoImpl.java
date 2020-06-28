package ru.otus.hw4.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw4.core.exception.QuestionBlockCreationException;
import ru.otus.hw4.core.question.QuestionBlock;
import ru.otus.hw4.core.question.QuestionDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionDaoImpl implements QuestionDao {
    private final QuestionBlockBuilder builder;

    @Override
    public List<QuestionBlock> getAllQuestionBlocks() throws QuestionBlockCreationException {
        return builder.build();
    }
}
