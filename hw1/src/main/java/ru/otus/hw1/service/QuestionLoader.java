package ru.otus.hw1.service;

import org.springframework.core.io.Resource;
import ru.otus.hw1.core.Answer;
import ru.otus.hw1.core.Question;
import ru.otus.hw1.core.QuestionBlock;
import ru.otus.hw1.core.exception.QuestionBlockCreationException;
import ru.otus.hw1.service.exception.QBCRuntimeException;
import ru.otus.hw1.utils.csv.CSVParser;
import ru.otus.hw1.utils.csv.CSVRecord;
import ru.otus.hw1.utils.csv.exception.MalformedCSVException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class QuestionLoader {
    private final Charset charset;
    private final Resource questionSrc;
    private final CSVParser parser;

    public QuestionLoader(Charset charset, Resource questionSrc, CSVParser parser) {
        this.charset = charset;
        this.questionSrc = questionSrc;
        this.parser = parser;
    }

    /**
     * Load csv data from given resource and parse it into list of question blocks
     * @return list of loaded question blocks
     */
    public List<QuestionBlock> load() throws QuestionBlockCreationException {
        try {
            return createQuestionBlockList(readCSV());
        }
        catch (QBCRuntimeException e) {
            throw new QuestionBlockCreationException(e.getMessage(), e.getCause());
        }
    }

    private List<CSVRecord> readCSV() throws QBCRuntimeException {
        try {
            return parser.parse(new BufferedReader(new InputStreamReader(
                            questionSrc.getInputStream(), charset)));
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
