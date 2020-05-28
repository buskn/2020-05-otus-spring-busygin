package ru.otus.hw1.question;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import ru.otus.hw1.meeting.Answer;
import ru.otus.hw1.meeting.Question;
import ru.otus.hw1.meeting.QuestionBlock;
import ru.otus.hw1.meeting.QuestionService;
import ru.otus.hw1.meeting.exception.QuestionBlockCreationException;
import ru.otus.hw1.question.exception.QBCRuntimeException;
import ru.otus.hw1.utils.csv.CSVParser;
import ru.otus.hw1.utils.csv.CSVRecord;
import ru.otus.hw1.utils.csv.exception.MalformedCSVException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Реализация сервиса вопросов
 */

@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private static final char
            CSV_FIELD_SEPARATOR = ';',
            CSV_FIELD_ENCLOSER = '"';

    private final Resource questionSrc;

    public QuestionServiceImpl(Resource questionSrc) {
        this.questionSrc = questionSrc;
    }

    /**
     * @return Список всех полученных блоков вопросов
     * @throws QuestionBlockCreationException при любой ошибке реконструирования блоков из источника
     */
    @Override
    public List<QuestionBlock> getAllQuestionBlocks() throws QuestionBlockCreationException {
        try {
            return createQuestionBlockList(readCSV());
        }
        catch (QBCRuntimeException e) {
            throw new QuestionBlockCreationException(e.getMessage(), e.getCause());
        }
    }

    private List<CSVRecord> readCSV() throws QBCRuntimeException {
        try {
            return new CSVParser(CSV_FIELD_SEPARATOR, CSV_FIELD_ENCLOSER)
                    .parse(new BufferedReader(new InputStreamReader(
                            questionSrc.getInputStream(), StandardCharsets.UTF_8)));
        }
        catch (MalformedCSVException | IOException e) {
            throw new QBCRuntimeException(e);
        }
    }

    private List<QuestionBlock> createQuestionBlockList(List<CSVRecord> CSVRecords)
            throws QBCRuntimeException
    {
        return CSVRecords.stream()
                .map(this::createQuestionBlock)
                .collect(toList());
    }

    private QuestionBlock createQuestionBlock(CSVRecord CSVRecord)
            throws QBCRuntimeException
    {
        if (CSVRecord.size() % 2 != 0 || CSVRecord.size() < 2)
            throw new QBCRuntimeException("Incompatible record size: " + CSVRecord.size());
        Question question = new QuestionImpl(CSVRecord.get(0));
        switch (CSVRecord.get(1).trim().toLowerCase()) {
            case "free" :
                return new QuestionBlockImpl(question);
            case "test":
                return new QuestionBlockImpl(question, createAnswers(CSVRecord));
            default:
                throw new QBCRuntimeException("question type is not 'free' or 'test'");
        }
    }

    private List<Answer> createAnswers(CSVRecord CSVRecord)
            throws QBCRuntimeException
    {
        List<Answer> result = new ArrayList<>();
        int num = 2; // первые два уже прочитаны, здесь начинаются ответы
        while (CSVRecord.size() > num) {
            String correctnessValue = CSVRecord.get(num).trim().toLowerCase();
            String answerTextValue = CSVRecord.get(num + 1).trim();
            boolean isCorrect;
            switch (correctnessValue) {
                case "correct": isCorrect = true; break;
                case "wrong" : isCorrect = false; break;
                default: throw new QBCRuntimeException(
                        "correctness not in 'correct' or 'wrong'");
            }
            result.add(new AnswerImpl(answerTextValue, isCorrect));
            num += 2;
        }
        return result;
    }
}
