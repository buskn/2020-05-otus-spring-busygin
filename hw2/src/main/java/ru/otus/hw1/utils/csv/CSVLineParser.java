package ru.otus.hw1.utils.csv;

import ru.otus.hw1.utils.csv.exception.MalformedCSVRuntimeException;

public interface CSVLineParser {
    CSVRecord parse(String line) throws MalformedCSVRuntimeException;
}
