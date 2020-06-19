package ru.otus.hw2.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.otus.hw2.core.exception.QuestionBlockCreationException;
import ru.otus.hw2.utils.csv.CsvLineParser;
import ru.otus.hw2.utils.csv.CsvRecord;
import ru.otus.hw2.utils.csv.exception.MalformedCSVException;
import ru.otus.hw2.utils.exception.WrapExceptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Load csv data from given resource
 */
@Service
public class QuestionSource {
    private final Charset charset;
    private final Resource questionSrc;
    private final CsvLineParser parser;

    public QuestionSource(
            @Value("${hw2.questions.file.charset:UTF-8}") Charset charset,
            @Value("${hw2.questions.file.filename:questions.csv}") Resource questionSrc,
            CsvLineParser parser
    ) {
        this.charset = charset;
        this.questionSrc = questionSrc;
        this.parser = parser;
    }

    /**
     * Load csv data from given resource
     * @return stream of loaded csv records
     */
    public Stream<CsvRecord> records() throws QuestionBlockCreationException {
        return constructReader()
                .lines()
                .filter(line -> !line.isBlank())
                .map(line -> rethrow(() -> parser.parse(line)));
    }

    public BufferedReader constructReader() throws QuestionBlockCreationException {
        try {
            return new BufferedReader(new InputStreamReader(
                    questionSrc.getInputStream(), charset));
        }
        catch (IOException e) {
            throw new QuestionBlockCreationException(e);
        }
    }

    public CsvRecord rethrow(Supplier<CsvRecord> supplier) {
        try {
            return supplier.get();
        }
        catch (MalformedCSVException | UncheckedIOException e) {
            throw new QuestionBlockCreationException(e);
        }
    }
}
