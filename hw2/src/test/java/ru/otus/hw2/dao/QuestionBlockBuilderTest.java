package ru.otus.hw2.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.otus.hw2.core.exception.QuestionBlockCreationException;
import ru.otus.hw2.core.question.Answer;
import ru.otus.hw2.core.question.Question;
import ru.otus.hw2.core.question.QuestionBlock;
import ru.otus.hw2.utils.csv.CsvRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class QuestionBlockBuilderTest {

    private QuestionBlockBuilder builder;

    @Test
    void givenCorrectCsv_whenBuild_thenSuccess() {
        Map<CsvRecord, QuestionBlock> values = getCorrectValues();

        QuestionSource source = mock(QuestionSource.class);
        when(source.records()).thenReturn(values.keySet().stream());

        builder = new QuestionBlockBuilder(source);

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
        Map<CsvRecord, QuestionBlockCreationException> values = getCorruptedValues();

        values.forEach( (record, exc) -> {
            var source = mock(QuestionSource.class);
            when(source.records()).thenReturn( Stream.of(record) );

            builder = new QuestionBlockBuilder(source);

            assertThatExceptionOfType(values.get(record).getClass())
                    .isThrownBy(() -> builder.build().forEach(x -> {}))
                    .withMessage(values.get(record).getMessage());
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