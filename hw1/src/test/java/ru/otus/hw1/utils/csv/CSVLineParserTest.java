package ru.otus.hw1.utils.csv;

import org.junit.jupiter.api.Test;
import ru.otus.hw1.utils.csv.exception.MalformedCSVException;
import ru.otus.hw1.utils.csv.exception.MalformedCSVRuntimeException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CSVLineParserTest {

    private static final char SEP = ';', ENC = '"';

    @Test
    void givenBadQuotedCSV_whenParse_thenThrows() {
        List.of(
                "\"111\";\"222\";\"333", // no quote in the end
                "111\";\"222\";\"333\"", // no quote in the beginning
                "\"111\";222\";\"333\"", // no quote in the beginning of 2nd field
                "  \"  111  \"  ;\"222\"; \" 333  ", // no quote in the end, spaces added
                "  111 \" ;\"222\";\"333\"", // no quote in the beginning, spaces added
                "\"  111\";  222  \" ;\"333\"" // no quote in the beginning of 2nd field, spaces added
        ).forEach( str ->
                assertThatExceptionOfType(MalformedCSVRuntimeException.class)
                        .describedAs("'%s' is wrongly correct", str)
                        .isThrownBy(new CSVLineParser(SEP, ENC, str)::parse)
                        .withMessageContaining(str)
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

                ";", new CSVRecord(List.of("")),

                "  ;  ", new CSVRecord(List.of("")),

                "", new CSVRecord(List.of())
        ).forEach( (str, rec) ->
                assertThat(new CSVLineParser(SEP, ENC, str).parse())
                        .isEqualTo(rec)
        );
    }
}