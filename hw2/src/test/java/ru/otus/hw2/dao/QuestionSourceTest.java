package ru.otus.hw2.dao;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.Resource;
import ru.otus.hw2.core.exception.QuestionBlockCreationException;
import ru.otus.hw2.utils.csv.CsvLineParser;
import ru.otus.hw2.utils.csv.CsvRecord;
import ru.otus.hw2.utils.csv.exception.MalformedCSVException;
import ru.otus.hw2.utils.exception.WrapExceptionsAspect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class QuestionSourceTest {
    private static final Charset charset = StandardCharsets.UTF_8;

    @Test
    void givenSourceWithEmptyStrings_whenRecords_thenIgnoreEmptyStrings() throws IOException {
        String content = "\n1\n\n\n2\n\n";
        InputStream input = new ByteArrayInputStream(content.getBytes(charset));

        Resource res = mock(Resource.class);
        when(res.getInputStream()).thenReturn(input);

        CsvLineParser parser = mock(CsvLineParser.class);
        when(parser.parse(any())).thenReturn(new CsvRecord());

        QuestionSource source = new QuestionSource(charset, res, parser);

        assertThat(source.records()).hasSize(2);
    }

    @Test
    void givenCorrectSource_whenRecords_thenSuccess() throws IOException {
        var values = Map.of(
                "1;2;3", new CsvRecord(List.of("1", "2", "3")),
                "4;5;6", new CsvRecord(List.of("4", "5", "6")),
                "7;8;9", new CsvRecord(List.of("7", "8", "9"))
        );

        var content = String.join("\n", values.keySet());
        var input = new ByteArrayInputStream(content.getBytes(charset));
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(input);

        CsvLineParser parser = mock(CsvLineParser.class);
        when(parser.parse(any())).then(x -> values.get(x.<String>getArgument(0)));

        var source = new QuestionSource(charset, resource, parser);

        assertEquals(source.records().collect(toList()),
                new ArrayList<>(values.values()));
    }

    @Test
    void givenCorruptedSource_whenRecords_thenThrows() throws IOException {
        String corrupted = "\"4;5;6";
        var values = List.of("1;2;3", corrupted, "7;8;9");

        var content = String.join("\n", values);
        var input = new ByteArrayInputStream(content.getBytes(charset));
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(input);

        CsvLineParser parser = mock(CsvLineParser.class);
        when(parser.parse( any() ))
                .thenReturn(new CsvRecord())
                .thenThrow(new MalformedCSVException(corrupted));

        var source = new QuestionSource(charset, resource, parser);

        assertThatExceptionOfType(QuestionBlockCreationException.class)
                .isThrownBy(() -> source.records().forEach(x -> {}))
                .withCauseInstanceOf(MalformedCSVException.class)
                .withMessageContaining(corrupted);
    }

}