package ru.otus.hw4.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw4.core.exception.QuestionBlockCreationException;
import ru.otus.hw4.core.question.Answer;
import ru.otus.hw4.core.question.QuestionBlock;
import ru.otus.hw4.core.question.Question;
import ru.otus.hw4.utils.csv.CsvRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Создает список блоков вопросов на основе csv-данных, подготовленных {@link QuestionSource}
 */
@Service
@RequiredArgsConstructor
public class QuestionBlockBuilder {
    private final QuestionSource source;

    /**
     * @return Список блоков вопросов
     */
    public List<QuestionBlock> build() throws QuestionBlockCreationException {
        return source.records()
                .map(this::createQuestionBlock)
                .collect(toList());
    }

    private QuestionBlock createQuestionBlock(CsvRecord record)
            throws QuestionBlockCreationException
    {
        if (record.size() < 3)
            throw new QuestionBlockCreationException("Incompatible record size: " + record.size());
        Question question = new Question(record.get(0));
        switch (record.get(1).trim().toLowerCase()) {
            case "free" :
                return new QuestionBlock(question, createFreeFormAnswers(record), true);
            case "test":
                return new QuestionBlock(question, createTestFormAnswers(record), false);
            default:
                throw new QuestionBlockCreationException("question type is not 'free' or 'test'");
        }
    }

    private List<Answer> createTestFormAnswers(CsvRecord record)
            throws QuestionBlockCreationException
    {
        if (record.size() % 2 != 0)
            throw new QuestionBlockCreationException("Incompatible record size: " + record.size());
        List<Answer> result = new ArrayList<>();
        for (int num = 2 /* answers start */; num < record.size(); num += 2 /* answer group size */) {
            String correctnessValue = record.get(num).trim().toLowerCase();
            String answerTextValue = record.get(num + 1).trim();
            boolean isCorrect;
            switch (correctnessValue) {
                case "correct": isCorrect = true; break;
                case "wrong" : isCorrect = false; break;
                default: throw new QuestionBlockCreationException(
                        "correctness not in 'correct' or 'wrong'");
            }
            result.add(new Answer(answerTextValue, isCorrect));
        }
        return result;
    }

    private List<Answer> createFreeFormAnswers(CsvRecord record)
            throws QuestionBlockCreationException
    {
        return record.stream()
                .skip(2) // answers start
                .map(text -> new Answer(text, true))
                .collect(Collectors.toList());
    }
}
