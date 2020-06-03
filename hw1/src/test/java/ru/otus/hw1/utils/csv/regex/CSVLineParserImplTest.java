package ru.otus.hw1.utils.csv.regex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw1.utils.csv.CSVLineParser;
import ru.otus.hw1.utils.csv.CSVRecord;
import ru.otus.hw1.utils.csv.exception.MalformedCSVRuntimeException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class CSVLineParserImplTest {

    private static final char SEP = ';', ENC = '"';
    private CSVLineParser parser;

    @BeforeEach
    void prepare() {
        parser = new CSVLineParserImpl(SEP, ENC);
    }

    @Test
    void givenBadQuotedCSV_whenParse_thenThrows() {
        List.of(
                "\"111\";\"222\";\"333", // no quote in the end
                "111\";\"222\";\"333\"", // no quote in the beginning
                "\"111\";222\";\"333\"", // no quote in the beginning of 2nd field
                "  \"  111  \"  ;\"222\"; \" 333  ", // no quote in the end, spaces added
                "  111 \" ;\"222\";\"333\"", // no quote in the beginning, spaces added
                "\"  111\";  222  \" ;\"333\"" // no quote in the beginning of 2nd field, spaces added

                // TODO FAILING!
                //"111\";\"222\";\"333", new CSVRecord(List.of()) // no quote in the begin and the end

        ).forEach( str ->
                assertThatExceptionOfType(MalformedCSVRuntimeException.class)
                        .describedAs("'%s' is wrongly correct", str)
                        .isThrownBy(() -> parser.parse(str))
                        .withMessageContaining(str)
        );
    }

    @Test
    void givenCorrectCSV_whenParse_thenNotThrowsAndNotNull() {
        List.of(
                "111;222;333",
                "\"111\";222;\"333\"",
                "  111  ;  \"222\"  ;  \"  333  \"  ",
                ";",
                "  ;  ",
                ""
        ).forEach( str ->
                assertThat(parser.parse(str))
                        .withFailMessage("'%s' is not correct")
                        .isNotNull()
        );
    }

    @Test
    void givenCorrectCSV_whenParse_thenSuccess() {
        Map.of(
                "111;222;333",
                new CSVRecord(List.of( "111", "222", "333" )),

                "\"111\";222;\"333\"",
                new CSVRecord(List.of( "111", "222", "333" )),

                "  111  ;  \"222\"  ;  \"  333  \"  ",
                new CSVRecord(List.of( "111", "222", "  333  " )),

                "  11 1  ;  \"22 2\"  ;  \"  33  3  \"  ",
                new CSVRecord(List.of( "11 1", "22 2", "  33  3  " )),

                ";", new CSVRecord(List.of("")),

                "  ;  ", new CSVRecord(List.of("")),

                "", new CSVRecord(List.of())
        ).forEach( (str, rec) ->
                assertThat(parser.parse(str))
                        .withFailMessage(
                                String.format("line: '%s'\nexcept: '%s'\nparsed: '%s' ",
                                        str, rec, parser.parse(str)))
                        .isEqualTo(rec)
        );
    }
}