package ru.otus.hw1.utils.csv;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Запись из CSV-файла
 */
public class CSVRecord extends ArrayList<String> {
    public CSVRecord() {}

    public CSVRecord(Collection<String> coll) {
        super(coll);
    }
}
