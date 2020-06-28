package ru.otus.hw3.dao;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import ru.otus.hw3.config.ValueAnnotatedAppSettings;
import ru.otus.hw3.core.exception.QuestionBlockCreationException;
import ru.otus.hw3.utils.csv.CsvLineParser;
import ru.otus.hw3.utils.csv.CsvRecord;
import ru.otus.hw3.utils.csv.exception.MalformedCSVException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

@SpringBootTest
@Slf4j
class QuestionSourceTest {

    @Configuration
    @Import({QuestionSource.class})
    private static class Config {}

    @Autowired
    private QuestionSource source;

    @MockBean
    private CsvLineParser parser;

    @MockBean
    private ValueAnnotatedAppSettings settings;

    @Mock
    private Resource resource;

    private static final Charset charset = StandardCharsets.UTF_8;

    @BeforeEach
    private void setUp() {
        when(settings.getQuestionFileCharset()).thenReturn(charset);
        when(settings.getQuestionFileResource()).thenReturn(resource);
    }

    @Test
    void givenSourceWithEmptyStrings_whenRecords_thenIgnoreEmptyStrings() throws IOException {
        val content = "\n1\n\n\n2\n\n";
        val input = new ByteArrayInputStream(content.getBytes(charset));

        when(resource.getInputStream()).thenReturn(input);
        when(parser.parse(any())).thenReturn(new CsvRecord());

        assertThat(source.records()).hasSize(2);
    }

    @Test
    void givenCorrectSource_whenRecords_thenSuccess() throws IOException {
        val values = Map.of(
                "1;2;3", new CsvRecord(List.of("1", "2", "3")),
                "4;5;6", new CsvRecord(List.of("4", "5", "6")),
                "7;8;9", new CsvRecord(List.of("7", "8", "9"))
        );

        val content = String.join("\n", values.keySet());
        val input = new ByteArrayInputStream(content.getBytes(charset));

        when(resource.getInputStream()).thenReturn(input);
        when(parser.parse(any())).then(x -> values.get(x.<String>getArgument(0)));

        assertEquals(source.records().collect(toList()),
                new ArrayList<>(values.values()));
    }

    @Test
    void givenCorruptedSource_whenRecords_thenThrows() throws IOException {
        val corrupted = "\"4;5;6";
        val values = List.of("1;2;3", corrupted, "7;8;9");

        val content = String.join("\n", values);
        val input = new ByteArrayInputStream(content.getBytes(charset));

        when(resource.getInputStream()).thenReturn(input);
        when(parser.parse( any() ))
                .thenReturn(new CsvRecord())
                .thenThrow(new MalformedCSVException(corrupted));

        assertThatExceptionOfType(QuestionBlockCreationException.class)
                .isThrownBy(() -> source.records().forEach(x -> {}))
                .withCauseInstanceOf(MalformedCSVException.class)
                .withMessageContaining(corrupted);
    }

}