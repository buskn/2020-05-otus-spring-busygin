package ru.otus.hw6.ui;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.config.Settings;
import ru.otus.hw6.data.model.Author;
import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;
import ru.otus.hw6.data.model.Genre;
import ru.otus.hw6.ui.commands.OperationManagementCommands;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OperationManagementCommandsTest {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Configuration
    @Import(OperationManagementCommands.class)
    static class Config {}

    @Autowired private OperationManagementCommands shell;

    @MockBean IO io;
    @MockBean Settings settings;
    @MockBean Settings.Ui ui;
    @MockBean ShellState state;

    private ByteArrayOutputStream outputStream;

    private final Book book1 = new Book.Builder()
            .setId(1)
            .setTitle("title1")
            .setAuthor(new Author(11, "author1"))
            .setGenres(List.of(new Genre(21, "genre1"), new Genre(22, "genre2")))
            .setComments(List.of(new Comment(11, "c1")))
            .build();
    private final Book book2 = new Book.Builder()
            .setId(2)
            .setTitle("title2")
            .setAuthor(new Author(12, "author2"))
            .setGenres(List.of(new Genre(23, "genre1"), new Genre(24, "genre2")))
            .setComments(List.of(new Comment(12, "c1")))
            .build();

    @BeforeEach
    private void setUp() {
        when(settings.getUi()).thenReturn(ui);

        outputStream = new ByteArrayOutputStream();
        when(io.print(any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.println(any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.interPrint(any(), any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.interPrintln(any(), any())).then(inv -> pushArgsToOut(inv.getArguments()));
    }

    @SneakyThrows
    private IO pushArgsToOut(Object [] args) {
        for (Object arg : args)
            outputStream.write((arg + "|").getBytes(CHARSET));
        return io;
    }

    @Test
    void givenNoArg_whenLanguage_thenShowCurrent() {
        val current = Locale.forLanguageTag("current");
        val acceptable = List.of(Locale.CHINESE, Locale.FRANCE);
        when(ui.getLocale()).thenReturn(current);
        when(ui.getAcceptableLocale()).thenReturn(acceptable);

        shell.language(null);

        assertThat(outputStream.toString(CHARSET))
                .contains(current.getDisplayLanguage())
                .contains(acceptable.toString());
    }

    @Test
    void givenAcceptLocale_whenLanguage_thenChange() {
        val choice = Locale.forLanguageTag("current");
        val acceptable = List.of(Locale.CHINESE, Locale.FRANCE, Locale.forLanguageTag("current"));
        when(ui.getAcceptableLocale()).thenReturn(acceptable);

        shell.language(choice);

        assertThat(outputStream.toString(CHARSET)).contains(choice.getDisplayCountry());
        val localeArg = ArgumentCaptor.forClass(Locale.class);
        verify(ui).setLocale(localeArg.capture());
        assertThat(localeArg.getValue()).isEqualTo(choice);
    }

    @Test
    void givenNotAcceptableLocale_whenLanguage_thenNoChange() {
        val choice = Locale.forLanguageTag("current");
        val acceptable = List.of(Locale.CHINESE, Locale.FRANCE);
        when(ui.getAcceptableLocale()).thenReturn(acceptable);

        shell.language(choice);

        verify(ui, never()).setLocale(any());
    }
}