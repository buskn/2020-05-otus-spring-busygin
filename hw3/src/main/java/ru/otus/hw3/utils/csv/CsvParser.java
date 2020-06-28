package ru.otus.hw3.utils.csv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw3.utils.csv.exception.MalformedCSVException;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Парсер CSV данных
 */
@Service
@Slf4j
public class CsvParser {
    private final CsvLineParser parser;

    public CsvParser(CsvLineParser parser) {
        this.parser = parser;
    }

    public List<CsvRecord> parse(BufferedReader reader) throws MalformedCSVException {
        return reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(parser::parse)
                .collect(Collectors.toList());
    }

}
