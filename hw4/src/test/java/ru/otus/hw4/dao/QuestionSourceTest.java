package ru.otus.hw4.dao;

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
import ru.otus.hw4.config.AppSettings;
import ru.otus.hw4.core.exception.QuestionBlockCreationException;
import ru.otus.hw4.utils.csv.CsvLineParser;
import ru.otus.hw4.utils.csv.CsvRecord;
import ru.otus.hw4.utils.csv.exception.MalformedCSVException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class QuestionSourceTest {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Locale LOCALE = Locale.forLanguageTag("en");

    @Configuration
    @Import(QuestionSource.class)
    static class Config {}

    @Autowired private QuestionSource source;
    @MockBean private CsvLineParser parser;
    @MockBean private AppSettings settings;
    @Mock private AppSettings.Questions questions;
    @Mock private AppSettings.Ui ui;

    @BeforeEach
    private void setUp() {
        when(questions.getCharset()).thenReturn(CHARSET);
        when(ui.getLocale()).thenReturn(LOCALE);

        when(settings.getQuestions()).thenReturn(questions);
        when(settings.getUi()).thenReturn(ui);
    }

    @Test
    void givenWrongFilename_whenRecords_thenThrow() {
        String filename = "questions_non_exists.csv";
        when(questions.getFilename()).thenReturn(filename);
        when(parser.parse(any())).thenReturn(new CsvRecord());

        assertThatExceptionOfType(QuestionBlockCreationException.class)
            .isThrownBy(source::records)
        .withMessageContaining(filename);
    }

    @Test
    void givenSourceWithEmptyStrings_whenRecords_thenIgnoreEmptyStrings() {
        when(questions.getFilename()).thenReturn("questions_empty_strings.csv");
        when(parser.parse(any())).thenReturn(new CsvRecord());

        assertThat(source.records()).hasSize(2);
    }

    @Test
    void givenCorrectSource_whenRecords_thenSuccess() {
        val values = new LinkedHashMap<String, CsvRecord>() {{
                put( "1;2;3", new CsvRecord(List.of("1", "2", "3")) );
                put( "4;5;6", new CsvRecord(List.of("4", "5", "6")) );
                put( "7;8;9", new CsvRecord(List.of("7", "8", "9")) );
        }};

        when(questions.getFilename()).thenReturn("questions_correct.csv");
        when(parser.parse(any())).then(x -> values.get(x.<String>getArgument(0)));

        assertEquals(source.records().collect(toList()),
                new ArrayList<>(values.values()));
    }

    @Test
    void givenCorruptedSource_whenRecords_thenThrows() {
        val corrupted = "\"4;5;6";

        when(questions.getFilename()).thenReturn("questions_corrupted.csv");
        when(parser.parse( corrupted ))
                .thenThrow(new MalformedCSVException(corrupted));

        assertThatExceptionOfType(QuestionBlockCreationException.class)
                .isThrownBy(() -> source.records().forEach(x -> {}))
                .withCauseInstanceOf(MalformedCSVException.class)
                .withMessageContaining(corrupted);
    }

}