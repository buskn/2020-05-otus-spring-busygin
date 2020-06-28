package ru.otus.hw3.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw3.core.exception.QuestionBlockCreationException;
import ru.otus.hw3.core.question.Answer;
import ru.otus.hw3.core.question.Question;
import ru.otus.hw3.core.question.QuestionBlock;
import ru.otus.hw3.utils.csv.CsvRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
class QuestionBlockBuilderTest {

    @Configuration
    @Import(QuestionBlockBuilder.class)
    static class Config {}

    @Autowired
    private QuestionBlockBuilder builder;

    @MockBean
    private QuestionSource source;

    @Test
    void givenCorrectCsv_whenBuild_thenSuccess() {
        var values = getCorrectValues();

        when(source.records()).thenReturn(values.keySet().stream());

        assertThat(builder.build())
                .isEqualTo(new ArrayList<>(values.values()));
    }

    private Map<CsvRecord, QuestionBlock> getCorrectValues() {
        return Map.of(
                new CsvRecord(List.of("quest", "free", "ans1", "ans2")),
                new QuestionBlock(
                        new Question("quest"),
                        List.of(new Answer("ans1", true),
                                new Answer("ans2", true)),
                        true),

                new CsvRecord(List.of("quest", "test", "correct", "ans1", "wrong", "ans2")),
                new QuestionBlock(
                        new Question("quest"),
                        List.of(new Answer("ans1", true),
                                new Answer("ans2", false)),
                        false)
        );
    }

    @Test
    void givenCorruptedCsv_whenBuild_thenThrows() {
        getCorruptedValues().forEach( (record, exc) -> {
            when(source.records()).thenReturn( Stream.of(record) );

            assertThatExceptionOfType(exc.getClass())
                    .isThrownBy(() -> builder.build().forEach(x -> {}))
                    .withMessage(exc.getMessage());
        });
    }

    private Map<CsvRecord, QuestionBlockCreationException> getCorruptedValues() {
        return Map.of(
                new CsvRecord(List.of("quest", "free")), // small size
                new QuestionBlockCreationException("Incompatible record size: 2"),

                new CsvRecord(List.of("quest", "test")), // small size
                new QuestionBlockCreationException("Incompatible record size: 2"),

                new CsvRecord(List.of("quest", "fruu", "ans1", "ans2")), // "fruu"
                new QuestionBlockCreationException("question type is not 'free' or 'test'"),

                new CsvRecord(List.of("quest", "test", "correct", "ans1", "corruct", "ans2")), // "corruct"
                new QuestionBlockCreationException("correctness not in 'correct' or 'wrong'"),

                new CsvRecord(List.of("quest", "test", "wrong", "ans1", "corruct", "ans2")), // "corruct"
                new QuestionBlockCreationException("correctness not in 'correct' or 'wrong'")
        );
    }

}