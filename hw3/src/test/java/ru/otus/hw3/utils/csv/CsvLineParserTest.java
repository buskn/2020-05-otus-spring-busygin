package ru.otus.hw3.utils.csv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw3.config.ValueAnnotatedAppSettings;
import ru.otus.hw3.utils.csv.exception.MalformedCSVException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest
class CsvLineParserTest {

    @Configuration
    @Import(CsvLineParser.class)
    private static class Config {}

    private static final char SEP = ';', ENC = '"';

    @Autowired
    private CsvLineParser parser;

    @MockBean
    private ValueAnnotatedAppSettings settings;

    @BeforeEach
    private void setUp() {
        when(settings.getQuestionFileSeparator()).thenReturn(SEP);
        when(settings.getQuestionFileEncloser()).thenReturn(ENC);
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
        ).forEach( str ->
                assertThatExceptionOfType(MalformedCSVException.class)
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
                new CsvRecord(List.of( "111", "222", "333")),

                "\"111\";222;\"333\"",
                new CsvRecord(List.of( "111", "222", "333")),

                "  111  ;  \"222\"  ;  \"  333  \"  ",
                new CsvRecord(List.of( "111", "222", "  333  ")),

                "  11 1  ;  \"22 2\"  ;  \"  33  3  \"  ",
                new CsvRecord(List.of( "11 1", "22 2", "  33  3  ")),

                ";", new CsvRecord(List.of("")),

                "  ;  ", new CsvRecord(List.of("")),

                "", new CsvRecord(List.of())
        ).forEach( (str, rec) ->
                assertThat(parser.parse(str))
                        .withFailMessage(
                                String.format("line: '%s'\nexcept: '%s'\nparsed: '%s' ",
                                        str, rec, parser.parse(str)))
                        .isEqualTo(rec)
        );
    }
}