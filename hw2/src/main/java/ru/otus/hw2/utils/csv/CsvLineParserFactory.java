package ru.otus.hw2.utils.csv;

import ru.otus.hw2.utils.csv.fsm.CsvLineParserImpl;

/**
 * Фабрика для парсеров строк в формате CSV
 * @deprecated Фабрикой теперь выступает контейнер
 */
@Deprecated
public class CsvLineParserFactory {
    private final char
            fieldSeparator,
            fieldEncloser;

    public CsvLineParserFactory(char fieldSeparator, char fieldEncloser) {
        this.fieldSeparator = fieldSeparator;
        this.fieldEncloser = fieldEncloser;
    }

    CsvLineParser get() {
        return new CsvLineParserImpl(fieldSeparator, fieldEncloser);
    }
}
