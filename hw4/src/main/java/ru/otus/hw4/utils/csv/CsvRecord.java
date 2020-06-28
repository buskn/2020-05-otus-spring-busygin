package ru.otus.hw4.utils.csv;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Запись из CSV-файла
 */
public class CsvRecord extends ArrayList<String> {
    public CsvRecord() {}

    public CsvRecord(Collection<String> coll) {
        super(coll);
    }
}
