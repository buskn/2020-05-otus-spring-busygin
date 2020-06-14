package ru.otus.hw2.core.question;

import ru.otus.hw2.core.exception.QuestionBlockCreationException;

import java.util.List;

public interface QuestionDao {
    public List<QuestionBlock> getAllQuestionBlocks() throws QuestionBlockCreationException;
}
