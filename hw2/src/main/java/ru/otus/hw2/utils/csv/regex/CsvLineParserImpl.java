package ru.otus.hw2.utils.csv.regex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw2.utils.csv.CsvLineParser;
import ru.otus.hw2.utils.csv.CsvRecord;
import ru.otus.hw2.utils.csv.exception.MalformedCSVException;

import java.util.regex.Pattern;

/**
 * Класс для разбора одной CSV-строки в экземпляр CSVRecord
 */
@Service("csvLineParserRegex")
@Slf4j
public class CsvLineParserImpl implements CsvLineParser {
    private final char
            sep,
            encl;

    private final String simpleGroup;
    private final String quotedGroup;
    private final String someGroup;
    private final Pattern groupPattern;
    private final Pattern wholePattern;

    public CsvLineParserImpl(
            @Value("${hw2.questions.file.separator:;}") char sep,
            @Value("${hw2.questions.file.encloser:\"}") char encl
    ) {
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
    public CsvRecord parse(String line) throws MalformedCSVException {
        if ( ! wholePattern.matcher(line).matches() )
            throw new MalformedCSVException("on line '" + line + "'");

        CsvRecord rec =  groupPattern.matcher(line)
                .results()
                .map(r -> coalesce(r.group(1), r.group(2), ""))
                .map(String::trim)
                .map(this::removeQuotes)
                .collect(CsvRecord::new, CsvRecord::add, CsvRecord::addAll);
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
