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

    private final CSVRecord record = new CSVRecord();
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

        abstract void handle(char c) throws MalformedCSVRuntimeException;

        @Override
        public String toString() {
            return "State{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    private StringBuilder spaceAcc = new StringBuilder();

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
        public void handle(char c) throws MalformedCSVRuntimeException {
            if (c == sep) {
                storeField();
                state = begin;
            }
            else if (c == encl) {
                throw new MalformedCSVRuntimeException(getExcMessage());
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
        public void handle(char c) throws MalformedCSVRuntimeException {
            if (Character.isSpaceChar(c)) {
                spaceAcc.append(c);
            }
            else if (c == sep) {
                storeField();
                spaceAcc = new StringBuilder();
                state = begin;
            }
            else if (c == encl) {
                throw new MalformedCSVRuntimeException(getExcMessage());
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
        public void handle(char c) throws MalformedCSVRuntimeException {
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
                throw new MalformedCSVRuntimeException(getExcMessage());
            }
        }
    };
    private final State enclosedEndSpaces = new State("enclosedEndSpaces") {
        @Override
        public void handle(char c) throws MalformedCSVRuntimeException {
            if (c == sep) {
                storeField();
                state = begin;
            }
            else if ( ! Character.isSpaceChar(c) ) {
                throw new MalformedCSVRuntimeException(getExcMessage());
            }
        }
    };

    private State state = begin;

    private void storeField() {
        record.add(fieldBld.toString());
        fieldBld = new StringBuilder();
    }

    private String getExcMessage() {
        return "on line '" + line + "', pos " + pos + ", state " + state;
    }
}
