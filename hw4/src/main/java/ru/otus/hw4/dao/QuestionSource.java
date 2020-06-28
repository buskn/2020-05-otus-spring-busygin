package ru.otus.hw4.dao;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.otus.hw4.config.AppSettings;
import ru.otus.hw4.utils.verification.Verifiable;
import ru.otus.hw4.utils.verification.VerifyException;
import ru.otus.hw4.core.exception.QuestionBlockCreationException;
import ru.otus.hw4.utils.csv.CsvLineParser;
import ru.otus.hw4.utils.csv.CsvRecord;
import ru.otus.hw4.utils.csv.exception.MalformedCSVException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Load csv data from given resource
 */
@Service
public class QuestionSource implements ResourceLoaderAware, Verifiable {
    private ResourceLoader resourceLoader;
    private final AppSettings settings;
    private final CsvLineParser parser;

    public QuestionSource(
            AppSettings settings,
            CsvLineParser parser
    ) {
        this.settings = settings;
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

    private BufferedReader constructReader() throws QuestionBlockCreationException {
        try {
            return new BufferedReader(new InputStreamReader(
                    getSrcResource().getInputStream(),
                    settings.getQuestions().getCharset()));
        }
        catch (IOException e) {
            throw new QuestionBlockCreationException(e);
        }
    }

    private Resource getSrcResource() throws QuestionBlockCreationException {
        String path = settings.getQuestions().getFilename();
        Resource res = resourceLoader.getResource(path);
        if (!res.exists())
            throw new QuestionBlockCreationException(
                    "question template file not found: " + path);
        return res;
    }

    public CsvRecord rethrow(Supplier<CsvRecord> supplier) throws QuestionBlockCreationException {
        try {
            return supplier.get();
        }
        catch (MalformedCSVException | UncheckedIOException e) {
            throw new QuestionBlockCreationException(e);
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void verify() throws VerifyException {
        getSrcResource(); // test localized question file exists
    }
}
