package ru.otus.hw1.utils.csv;

import ru.otus.hw1.utils.csv.fsm.CSVLineParserImpl;

public class CSVLineParserFactory {
    private final char
            fieldSeparator,
            fieldEncloser;

    public CSVLineParserFactory(char fieldSeparator, char fieldEncloser) {
        this.fieldSeparator = fieldSeparator;
        this.fieldEncloser = fieldEncloser;
    }

    CSVLineParser get() {
        return new CSVLineParserImpl(fieldSeparator, fieldEncloser);
    }
}
