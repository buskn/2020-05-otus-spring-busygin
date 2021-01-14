package ru.otus.hw6.ui;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.dao.Genre;
import ru.otus.hw6.dao.GenreDao;
import ru.otus.hw6.ui.commands.GenreCommands;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GenreCommandsTest {
    @Configuration
    @Import(GenreCommands.class)
    static class Config {}

    @Autowired GenreCommands shell;

    @MockBean GenreDao genreDao;
    @MockBean IO io;
    @MockBean ShellState state;

    ByteArrayOutputStream out;

    private final Genre genre1 = new Genre(1, "genre1");
    private final Genre genre2 = new Genre(2, "genre2");
    private final Genre genre3 = new Genre(3, "genre3");
    private final List<Genre> genres = List.of(genre1, genre2, genre3);

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        when(io.print(any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.println(any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.printf(any(), any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.interPrint(any(), any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.interPrintln(any(), any())).then(inv -> pushArgsToOut(inv.getArguments()));
    }

    @SneakyThrows
    private IO pushArgsToOut(Object [] args) {
        for (Object arg : args)
            out.write((arg + "|").getBytes(UTF_8));
        return io;
    }

    @Test
    void whenAllGenres_thenSuccess() {
        when(genreDao.getAll()).thenReturn(genres);

        shell.allGenres();

        genres.forEach(g ->
                assertThat(out.toString(UTF_8)).contains(g.getGenre(), g.getId() + ""));
    }
}