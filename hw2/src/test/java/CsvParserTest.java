import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw2.utils.csv.CsvParser;
import ru.otus.hw2.utils.csv.CsvRecord;
import ru.otus.hw2.utils.csv.exception.MalformedCSVException;
import ru.otus.hw2.utils.csv.fsm.CsvLineParserImpl;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CsvParserTest {

    private CsvParser parser;

    @BeforeEach
    void prepare() {
        parser = new CsvParser(new CsvLineParserImpl(';', '"'));
    }

    @Test
    public void givenCorrectCSV_whenParse_thenSuccess() {
        Map.of(
                "1;2;3;4\n55;\"666\";\"777\"\"777\"",
                List.of(
                        new CsvRecord(List.of("1", "2", "3", "4")),
                        new CsvRecord(List.of("55", "666", "777\"777"))
                ),

                ";1;2;3;4\n;55;\"666\";\"777\"\"777\"",
                List.of(
                        new CsvRecord(List.of("", "1", "2", "3", "4")),
                        new CsvRecord(List.of("", "55", "666", "777\"777"))
                ),

                "1 ; 2; 3 ;  4  \n5 5;  \" 6 6 6 \"  ;  \" 7 7 7 \"\" 7 7 7 \"   ",
                List.of(
                        new CsvRecord(List.of("1", "2", "3", "4")),
                        new CsvRecord(List.of("5 5", " 6 6 6 ", " 7 7 7 \" 7 7 7 "))
                )
        ).forEach((text, expected) ->
                assertThatCode(() -> {
                    List<CsvRecord> parsed =
                            parser.parse(new BufferedReader(new StringReader(text)));
                    assertEquals(expected, parsed);
                })
                .doesNotThrowAnyException()
        );
    }

    @Test
    void givenMalformedCSV_whenParsing_thenThrows() {
        List.of(
                "1;2;3;4\n55;666\";\"777\"\"777\"", // no start quote in 666
                "1;2;3;4\n55;\"666;\"777\"\"777\"", // no end quote in 666
                "1;2;3;4\n55;666;\"777\"  \"777\"", // middle spaces in 777

                "1;2;3;4\n55; 666 \"  ;\"777\"\"777\"", // no start quote in 666, additional spaces
                "1;2;3;4\n55;  \"  666 ;\"777\"\"777\"", // no end quote in 666, additional spaces
                "1;2;3;4\n55;666;  \"  777 \"  \" 777 \"" // middle spaces in 777, additional spaces
        ).forEach(text ->
                assertThatExceptionOfType(MalformedCSVException.class)
                    .describedAs(text)
                    .isThrownBy(() ->
                            parser.parse(new BufferedReader(new StringReader(text))))
        );
    }
}
