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
    private final char
            fieldSeparator,
            fieldEncloser;

    public CSVParser(char fieldSeparator, char fieldEncloser) {
        this.fieldSeparator = fieldSeparator;
        this.fieldEncloser = fieldEncloser;
    }

    public List<CSVRecord> parse(BufferedReader reader) throws MalformedCSVException {
        try {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> new CSVLineParser(fieldSeparator, fieldEncloser, line).parse())
                    .collect(Collectors.toList());
        }
        catch (MalformedCSVRuntimeException e) {
            throw new MalformedCSVException(e.getMessage());
        }
    }

}
