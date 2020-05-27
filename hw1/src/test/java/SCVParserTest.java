import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import ru.otus.hw1.utils.CSVParser;
import ru.otus.hw1.utils.exception.MalformedCSVException;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

@Slf4j
public class SCVParserTest {

    @Test
    public void test() {
        CSVParser parser = new CSVParser(';', '"');
        String text = "1;2;3;4\n"
                + "55;\"666\";\"777\"\"777\"";
        try {
            parser.parse(new BufferedReader(new StringReader(text)))
                    .stream().map(List::toString).forEach(log::info);
        }
        catch (MalformedCSVException e) {
            Assert.fail();
        }

        text = "1;2;3\";4\n"
                + "55;\"666\";\"777\"\"777\"";
        try {
            parser.parse(new BufferedReader(new StringReader(text)));
            Assert.fail();
        }
        catch (MalformedCSVException e) {
            log.info("successfully thrown an exception");
        }
    }
}
