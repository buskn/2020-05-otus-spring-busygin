package ru.otus.hw3.utils.csv;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertJProxySetup;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw3.utils.csv.CsvParser;
import ru.otus.hw3.utils.csv.CsvRecord;
import ru.otus.hw3.utils.csv.exception.MalformedCSVException;
import ru.otus.hw3.utils.csv.CsvLineParser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
public class CsvParserTest {
    @Configuration
    @Import(CsvParser.class)
    static class Config {}

    @Autowired
    private CsvParser parser;

    @MockBean
    private CsvLineParser lineParser;

    @Test
    public void givenCorrectCSV_whenParse_thenSuccess() {
        getCorrectValues().forEach(map -> {
            map.forEach( (line, rec) ->
                    when(lineParser.parse(line)).thenReturn(rec));
            assertThatCode(() -> {
                List<CsvRecord> parsed =
                        parser.parse(new BufferedReader(
                                new StringReader(String.join("\n", map.keySet()))));
                assertEquals(map.values(), parsed);
            });
        });
    }

    private List<Map<String, CsvRecord>> getCorrectValues() {
        return List.of(
                Map.of(
                        "1;2;3;4",
                        new CsvRecord(List.of("1", "2", "3", "4")),

                        "55;\"666\";\"777\"\"777\"",
                        new CsvRecord(List.of("55", "666", "777\"777"))
                ),

                Map.of(
                        ";1;2;3;4",
                        new CsvRecord(List.of("", "1", "2", "3", "4")),

                        ";55;\"666\";\"777\"\"777\"",
                        new CsvRecord(List.of("", "55", "666", "777\"777"))
                ),

                Map.of(
                        "1 ; 2; 3 ;  4  ",
                        new CsvRecord(List.of("1", "2", "3", "4")),

                        "5 5;  \" 6 6 6 \"  ;  \" 7 7 7 \"\" 7 7 7 \"   ",
                        new CsvRecord(List.of("5 5", " 6 6 6 ", " 7 7 7 \" 7 7 7 "))
                )
        );
    }

    @Test
    void givenMalformedCSV_whenParsing_thenThrows() {
        List.of(
                set(1, "1;2;3;4", "55;666\";\"777\"\"777\""), // no start quote in 666
                set(1, "1;2;3;4", "55;\"666;\"777\"\"777\""), // no end quote in 666
                set(1, "1;2;3;4", "55;666;\"777\"  \"777\""), // middle spaces in 777

                set(1, "1;2;3;4", "55; 666 \"  ;\"777\"\"777\""), // no start quote in 666, additional spaces
                set(1, "1;2;3;4", "55;  \"  666 ;\"777\"\"777\""), // no end quote in 666, additional spaces
                set(1, "1;2;3;4", "55;666;  \"  777 \"  \" 777 \"") // middle spaces in 777, additional spaces
        ).forEach(s -> {
            when(lineParser.parse(s.getCorrupted()))
                    .thenThrow(new MalformedCSVException(s.getCorrupted()));
            assertThatExceptionOfType(MalformedCSVException.class)
                .describedAs(s.getCorrupted())
                .isThrownBy(() ->
                        parser.parse(new BufferedReader(new StringReader(s.getText()))));
        });
    }

    @Value
    private static class TestSet {
        private int error;
        private List<String> lines;

        String getText() {
            return String.join("\n", lines);
        }

        String getCorrupted() {
            return lines.get(error);
        }
    }

    private TestSet set(int error, String ... lines) {
        return new TestSet(error, Arrays.asList(lines));
    }
}
