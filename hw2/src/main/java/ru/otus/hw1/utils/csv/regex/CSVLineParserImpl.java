package ru.otus.hw1.utils.csv.regex;

import lombok.extern.slf4j.Slf4j;
import ru.otus.hw1.utils.csv.CSVLineParser;
import ru.otus.hw1.utils.csv.CSVRecord;
import ru.otus.hw1.utils.csv.exception.MalformedCSVRuntimeException;

import java.util.regex.Pattern;

/**
 * Класс для разбора одной CSV-строки в экземпляр CSVRecord
 */

@Slf4j
public class CSVLineParserImpl implements CSVLineParser {
    private final char
            sep,
            encl;

    private final String simpleGroup;
    private final String quotedGroup;
    private final String someGroup;
    private final Pattern groupPattern;
    private final Pattern wholePattern;

    public CSVLineParserImpl(char sep, char encl) {
        this.sep = sep;
        this.encl = encl;

        // TODO найти способ сделать это красиво
        simpleGroup =
                String.format("(?: (?<simple> [^%1$c%2$c]*[^\\s%1$c%2$c]+[^%1$c%2$c]*) %1$c? )", sep, encl);
        quotedGroup =
                String.format(
                        "(?: \\s* (?<quoted> %2$c (?: [^%2$c] | %2$c%2$c )* %2$c ) \\s* %1$c? )",
                        sep, encl);
        someGroup = String.format("(?: %s | %s | (?: \\s*%c) | (?: \\s+$))", simpleGroup, quotedGroup, sep);
        groupPattern = Pattern.compile(
                someGroup,
                Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
        wholePattern = Pattern.compile(
                        String.format("^ %s* $", someGroup),
                        Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
    }

    @Override
    public CSVRecord parse(String line) throws MalformedCSVRuntimeException {
        if ( ! wholePattern.matcher(line).matches() )
            throw new MalformedCSVRuntimeException("on line '" + line + "'");

        CSVRecord rec =  groupPattern.matcher(line)
                .results()
                .map(r -> coalesce(r.group(1), r.group(2), ""))
                .map(String::trim)
                .map(this::removeQuotes)
                .collect(CSVRecord::new, CSVRecord::add, CSVRecord::addAll);
        if (rec.size() > 1 && rec.get(rec.size() - 1).equals(""))
            rec.remove(rec.size() - 1);
        return rec;
    }

    private static String coalesce(String a, String b, String c) {
        return a != null ? a : (b != null ? b : c);
    }

    private String removeQuotes(String line) {
        if (line.indexOf(encl) == 0 && line.lastIndexOf(encl) == line.length() - 1) {
            String doubleQuote = "" + encl + encl,
                    singleQuote = "" + encl;
            line =  line.substring(1, line.length() - 1).replace(doubleQuote, singleQuote);
        }
        return line;
    }
}
