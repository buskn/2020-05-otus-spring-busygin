package ru.otus.hw3.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw3.core.exception.QuestionBlockCreationException;
import ru.otus.hw3.core.question.QuestionBlock;
import ru.otus.hw3.core.question.QuestionDao;

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
