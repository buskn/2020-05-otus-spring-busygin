package ru.otus.hw1.utils.csv;

import lombok.extern.slf4j.Slf4j;
import ru.otus.hw1.utils.csv.exception.MalformedCSVException;
import ru.otus.hw1.utils.csv.exception.MalformedCSVRuntimeException;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Парсер CSV данных
 */
@Slf4j
public class CSVParser {
    private final CSVLineParser parser;

    public CSVParser(CSVLineParser parser) {
        log.info("created with type of parser: " + parser.getClass().getCanonicalName());
        this.parser = parser;
    }

    public List<CSVRecord> parse(BufferedReader reader) throws MalformedCSVException {
        try {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(parser::parse)
                    .collect(Collectors.toList());
        }
        catch (MalformedCSVRuntimeException e) {
            throw new MalformedCSVException(e.getMessage());
        }
    }

}
