package ru.otus.hw1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import ru.otus.hw1.core.Answer;
import ru.otus.hw1.core.Question;
import ru.otus.hw1.core.QuestionBlock;
import ru.otus.hw1.core.QuestionService;
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

/**
 * Реализация сервиса вопросов
 */

@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionLoader loader;
    private List<QuestionBlock> blocks;

    public QuestionServiceImpl(QuestionLoader loader) {
        this.loader = loader;
    }

    /**
     * @return Список всех полученных блоков вопросов
     * @throws QuestionBlockCreationException при любой ошибке реконструирования блоков из источника
     */
    @Override
    public List<QuestionBlock> getAllQuestionBlocks() throws QuestionBlockCreationException {
        if (blocks == null)
            blocks = loader.load();
        return blocks;
    }

}
