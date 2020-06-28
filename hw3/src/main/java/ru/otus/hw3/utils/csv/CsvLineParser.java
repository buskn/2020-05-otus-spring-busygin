package ru.otus.hw3.utils.csv;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.otus.hw3.config.AppSettings;
import ru.otus.hw3.utils.csv.exception.MalformedCSVException;

/**
 * Класс для разбора одной CSV-строки в экземпляр CSVRecord
 */
@Service
@Primary
public class CsvLineParser {
    private final AppSettings settings;

    public CsvLineParser(AppSettings settings) {
        this.settings = settings;
    }

    private char sep;
    private char encl;
    private CsvRecord record;
    private StringBuilder fieldBld;
    private int pos;
    private StringBuilder spaceAcc = new StringBuilder();
    private String line;
    private State state;

    private void prepare(String line) {
        sep = settings.questions.separator;
        encl = settings.questions.encloser;
        state = begin;
        record = new CsvRecord();
        fieldBld = new StringBuilder();
        pos = 0;
        spaceAcc = new StringBuilder();
        this.line = line;
    }

    public CsvRecord parse(String line) throws MalformedCSVException {
        prepare(line);
        for (char c : line.toCharArray()) {
            pos++;
            state.handle(c);
        }
        if (state == enclosed) {
            throw new MalformedCSVException(getExcMessage());
        }
        if (state == simple || state == enclosedQuote || state == enclosedEndSpaces) {
            record.add(fieldBld.toString());
        }
        return record;
    }

    private abstract static class State {
        private final String name;

        public State(String name) {
            this.name = name;
        }

        abstract void handle(char c) throws MalformedCSVException;

        @Override
        public String toString() {
            return "State{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    private final State begin = new State("begin") {
        @Override
        public void handle(char c) {
            if (Character.isSpaceChar(c)) {
                /* ignore starting blanks */
            }
            else if (c == sep) {
                record.add(fieldBld.toString());
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
    private final State simple = new State("simple") {
        @Override
        public void handle(char c) throws MalformedCSVException {
            if (c == sep) {
                storeField();
                state = begin;
            }
            else if (c == encl) {
                throw new MalformedCSVException(getExcMessage());
            }
            else if (Character.isSpaceChar(c)) {
                spaceAcc.append(c);
                state = simpleSpaces;
            }
            else {
                fieldBld.append(c);
            }
        }
    };
    private final State simpleSpaces = new State("simpleSpaces") {
        @Override
        public void handle(char c) throws MalformedCSVException {
            if (Character.isSpaceChar(c)) {
                spaceAcc.append(c);
            }
            else if (c == sep) {
                storeField();
                spaceAcc = new StringBuilder();
                state = begin;
            }
            else if (c == encl) {
                throw new MalformedCSVException(getExcMessage());
            }
            else {
                fieldBld.append(spaceAcc);
                spaceAcc = new StringBuilder();
                fieldBld.append(c);
                state = simple;
            }
        }
    };
    private final State enclosed = new State("enclosed") {
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
    private final State enclosedQuote = new State("enclosedQuote") {
        @Override
        public void handle(char c) throws MalformedCSVException {
            if (c == encl) {
                fieldBld.append(encl);
                state = enclosed;
            }
            else if (c == sep) {
                storeField();
                state = begin;
            }
            else if (Character.isSpaceChar(c)) {
                state = enclosedEndSpaces;
            }
            else {
                throw new MalformedCSVException(getExcMessage());
            }
        }
    };
    private final State enclosedEndSpaces = new State("enclosedEndSpaces") {
        @Override
        public void handle(char c) throws MalformedCSVException {
            if (c == sep) {
                storeField();
                state = begin;
            }
            else if ( ! Character.isSpaceChar(c) ) {
                throw new MalformedCSVException(getExcMessage());
            }
        }
    };

    private void storeField() {
        record.add(fieldBld.toString());
        fieldBld = new StringBuilder();
    }

    private String getExcMessage() {
        return "on line '" + line + "', pos " + pos + ", state " + state;
    }
}
