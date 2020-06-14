package ru.otus.hw2.utils.csv;

import ru.otus.hw2.utils.csv.exception.MalformedCSVException;

public interface CsvLineParser {
    CsvRecord parse(String line) throws MalformedCSVException;
}
