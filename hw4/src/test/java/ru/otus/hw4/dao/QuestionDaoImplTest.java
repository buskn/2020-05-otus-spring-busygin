package ru.otus.hw4.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw4.core.exception.QuestionBlockCreationException;
import ru.otus.hw4.core.question.QuestionBlock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class QuestionDaoImplTest {

    @Configuration
    @Import(QuestionDaoImpl.class)
    static class Config {}

    @Autowired QuestionDaoImpl dao;

    @MockBean QuestionBlockBuilder builder;

    @Test
    void givenCorrectBuilder_whenGetAllQuestionBlocks_thenSuccess() {
        List<QuestionBlock> blocks = List.of(mock(QuestionBlock.class), mock(QuestionBlock.class));
        when(builder.build()).thenReturn(blocks);

        assertThat(dao.getAllQuestionBlocks()).isEqualTo(blocks);

        verify(builder, times(1)).build();
    }

    @Test
    void givenCorruptedBuilder_whenGetAllQuestionBlocks_thenThrows() {
        when(builder.build()).thenThrow(QuestionBlockCreationException.class);

        assertThatExceptionOfType(QuestionBlockCreationException.class)
            .isThrownBy(() -> dao.getAllQuestionBlocks());

        verify(builder, times(1)).build();
    }

}