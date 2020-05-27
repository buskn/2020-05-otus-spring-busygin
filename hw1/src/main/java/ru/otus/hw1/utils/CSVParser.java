package ru.otus.hw1.utils;

import lombok.extern.slf4j.Slf4j;
import ru.otus.hw1.utils.exception.MalformedCSVException;
import ru.otus.hw1.utils.exception.MalformedCSVRuntimeException;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CSVParser {
    private final char
            fieldSeparator,
            fieldEncloser;

    public CSVParser(char fieldSeparator, char fieldEncloser) {
        this.fieldSeparator = fieldSeparator;
        this.fieldEncloser = fieldEncloser;
    }

    public List<List<String>> parse(BufferedReader reader) throws MalformedCSVException {
        try {
            return reader.lines()
                    .map(this::toList)
                    .collect(Collectors.toList());
        }
        catch (MalformedCSVRuntimeException e) {
            throw new MalformedCSVException(e.getMessage());
        }
    }

    private enum State { BEGIN, SIMPLE, ENCLOSED, QUOTE }
    private List<String> toList(String line) throws MalformedCSVRuntimeException {
        List<String> result = new ArrayList<>();
        State state = State.BEGIN;
        StringBuilder bld = new StringBuilder();
        for (char c : line.toCharArray()) {
            switch (state) {
                case BEGIN:
                    if (c == fieldSeparator) {
                        result.add(bld.toString());
                    }
                    else if (c == fieldEncloser) {
                        state = State.ENCLOSED;
                    }
                    else {
                        bld.append(c);
                        state = State.SIMPLE;
                    }
                    break;
                case SIMPLE:
                    if (c == fieldSeparator) {
                        result.add(bld.toString());
                        bld = new StringBuilder();
                        state = State.BEGIN;
                    }
                    else if (c == fieldEncloser) {
                        throw new MalformedCSVRuntimeException("on line: '" + line + "'");
                    }
                    break;
                case ENCLOSED:
                    if (c == fieldEncloser) {
                        state = State.QUOTE;
                    }
                    else {
                        bld.append(c);
                    }
                    break;
                case QUOTE:
                    if (c == fieldEncloser) {
                        bld.append(fieldEncloser);
                        state = State.ENCLOSED;
                    }
                    else if (c == fieldSeparator) {
                        result.add(bld.toString());
                        bld = new StringBuilder();
                        state = State.BEGIN;
                    }
                    else {
                        throw new MalformedCSVRuntimeException("on line: '" + line + "'");
                    }
                    break;
            }
        }
        if (state == State.ENCLOSED) {
            throw new MalformedCSVRuntimeException("on line: '" + line + "'");
        }
        if (state == State.SIMPLE || state == State.QUOTE) {
            result.add(bld.toString());
        }
        return result;
    }
}
