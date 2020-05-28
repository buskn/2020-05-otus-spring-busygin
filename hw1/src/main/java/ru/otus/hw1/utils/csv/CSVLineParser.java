package ru.otus.hw1.utils.csv;

import ru.otus.hw1.utils.csv.exception.MalformedCSVRuntimeException;

/**
 * Класс для разбора одной CSV-строки в экземпляр CSVRecord
 */
class CSVLineParser {
    private final char
            sep,
            encl;
    private final String line;

    public CSVLineParser(char sep, char encl, String line) {
        this.sep = sep;
        this.encl = encl;
        this.line = line;
    }

    private final CSVRecord CSVRecord = new CSVRecord();
    private StringBuilder fieldBld = new StringBuilder();
    private int pos;

    public CSVRecord parse() throws MalformedCSVRuntimeException {
        for (char c : line.toCharArray()) {
            pos++;
            state.handle(c);
        }
        if (state == enclosed) {
            throw new MalformedCSVRuntimeException(getExcMessage());
        }
        if (state == simple || state == enclosedQuote) {
            CSVRecord.add(fieldBld.toString());
        }
        return CSVRecord;
    }

    private interface State {
        void handle(char c) throws MalformedCSVRuntimeException;
    }

    private final State begin = new State() {
        @Override
        public void handle(char c) {
            if (c == sep) {
                CSVRecord.add(fieldBld.toString());
            }
            else if (c == encl) {
                state = enclosed;
            }
            else {
                fieldBld.append(c);
                state = simple;
            }
        }
    };
    private final State simple = new State() {
        @Override
        public void handle(char c) throws MalformedCSVRuntimeException {
            if (c == sep) {
                CSVRecord.add(fieldBld.toString());
                fieldBld = new StringBuilder();
                state = begin;
            }
            else if (c == encl) {
                throw new MalformedCSVRuntimeException(getExcMessage());
            }
            else {
                fieldBld.append(c);
            }
        }
    };
    private final State enclosed = new State() {
        @Override
        public void handle(char c) {
            if (c == encl) {
                state = enclosedQuote;
            }
            else {
                fieldBld.append(c);
            }
        }
    };
    private final State enclosedQuote = new State() {
        @Override
        public void handle(char c) throws MalformedCSVRuntimeException {
            if (c == encl) {
                fieldBld.append(encl);
                state = enclosed;
            }
            else if (c == sep) {
                CSVRecord.add(fieldBld.toString());
                fieldBld = new StringBuilder();
                state = begin;
            }
            else {
                throw new MalformedCSVRuntimeException(getExcMessage());
            }
        }
    };

    private State state = begin;

    private String getExcMessage() {
        return "on line '" + line + "', pos " + pos;
    }
}
