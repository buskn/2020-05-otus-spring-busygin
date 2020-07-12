package ru.otus.hw5.ui;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw5.dao.Author;
import ru.otus.hw5.dao.AuthorDao;
import ru.otus.hw5.ui.commands.AuthorCommands;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthorCommandsTest {
    @Configuration
    @Import(AuthorCommands.class)
    static class Config {}

    @Autowired AuthorCommands shell;

    @MockBean AuthorDao dao;
    @MockBean IO io;
    @MockBean ShellState state;

    ByteArrayOutputStream out;

    private final Author author1 = new Author(1, "author1");
    private final Author author2 = new Author(2, "author2");
    private final Author author3 = new Author(3, "author3");
    private final List<Author> authors = List.of(author1, author2, author3);

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
    void ctxRaised() {}

    @Test
    void whenAllGenres_thenSuccess() {
        when(dao.getAll()).thenReturn(authors);

        shell.allAuthors();

        authors.forEach(a ->
                assertThat(out.toString(UTF_8)).contains(a.getName(), a.getId() + ""));
    }
}