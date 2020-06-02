import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.otus.hw1.utils.csv.CSVParser;
import ru.otus.hw1.utils.csv.CSVRecord;
import ru.otus.hw1.utils.csv.exception.MalformedCSVException;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SCVParserTest {

    @Test
    public void test() {
        CSVParser parser = new CSVParser(';', '"');
        String text = "1;2;3;4\n"
                + "55;\"666\";\"777\"\"777\"";
        try {
            List<CSVRecord> parsed = parser.parse(new BufferedReader(new StringReader(text)));
            List<CSVRecord> sample = List.of(
                    new CSVRecord(List.of("1", "2", "3", "4")),
                    new CSVRecord(List.of("55", "666", "777\"777"))
            );
            assertEquals(parsed, sample);
        }
        catch (Throwable e) {
            log.error(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            fail();
        }

        text = "1;2;3\";4\n"
                + "55;\"666\";\"777\"\"777\"";
        try {
            parser.parse(new BufferedReader(new StringReader(text)));
            fail();
        }
        catch (MalformedCSVException e) {
            assertEquals(e.getMessage(), "on line '1;2;3\";4', pos 6");
        }
    }
}
