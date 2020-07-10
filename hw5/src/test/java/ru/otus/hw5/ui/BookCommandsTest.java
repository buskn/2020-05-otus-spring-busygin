package ru.otus.hw5.ui;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw5.dao.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookCommandsTest {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Configuration
    @Import(BookCommands.class)
    static class Config {}

    @Autowired private BookCommands shell;

    @MockBean IO io;
    @MockBean BookDao bookDao;
    @MockBean AuthorDao authorDao;
    @MockBean GenreDao genreDao;
    @MockBean ShellState state;
    @MockBean Book.Builder builder;

    private ByteArrayOutputStream outputStream;

    private final Book book1 = new Book(1, "title1",
            new Author(11, "author1"),
            List.of(new Genre(21, "genre1"), new Genre(22, "genre2")));
    private final Book book2 = new Book(2, "title2",
            new Author(12, "author2"),
            List.of(new Genre(23, "genre3"), new Genre(24, "genre4")));

    @BeforeEach
    @SneakyThrows
    private void setUp() {
        FieldSetter.setField(shell, shell.getClass().getDeclaredField("bookBuilder"), builder);

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
    void givenExistId_whenDeleteBook_thenSuccess() {
        val id = 1L;
        when(bookDao.getById(id)).thenReturn(Optional.of(book1));

        shell.deleteBook(id);

        verify(bookDao).delete(id);
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.deleted");
    }

    @Test
    void givenUnknownId_whenDeleteBook_thenSuccess() {
        val id = 99L;
        when(bookDao.getById(id)).thenReturn(Optional.empty());

        shell.deleteBook(id);

        // TODO WTF?!?!
//        verify(bookDao, never()).delete(any());
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.not-found");
    }

    @Test
    @SneakyThrows
    void whenShowBookState_thenSuccess() {
        val title = "someTitle";
        val author = mock(Author.class);
        when(author.getName()).thenReturn("authorName");
        val genre1 = mock(Genre.class);
        when(genre1.getGenre()).thenReturn("genre1");
        val genre2 = mock(Genre.class);
        when(genre2.getGenre()).thenReturn("genre2");
        Book.Builder builder = mock(Book.Builder.class);
        when(builder.getTitle()).thenReturn(Optional.of(title));
        when(builder.getAuthor()).thenReturn(Optional.of(author));
        when(builder.getGenres()).thenReturn(Set.of(genre1, genre2));

        FieldSetter.setField(shell, shell.getClass().getDeclaredField("bookBuilder"), builder);
        shell.show();

        assertThat(outputStream.toString(CHARSET))
                .contains(title, author.getName(), genre1.getGenre(), genre2.getGenre());
    }

    @Test
    @SneakyThrows
    void givenTitle_whenSetTitle_thenSuccess() {
        val title = "someTitle";
        val builder = mock(Book.Builder.class);
        FieldSetter.setField(shell, shell.getClass().getDeclaredField("bookBuilder"), builder);

        shell.setTitle(title);

        verify(builder).setTitle(title);
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.ok");
    }

    @Test
    @SneakyThrows
    void givenEmptyTitle_whenSetTitle_thenSuccess() {
        val title = "someTitle";
        val builder = mock(Book.Builder.class);
        FieldSetter.setField(shell, shell.getClass().getDeclaredField("bookBuilder"), builder);
        when(io.readLine()).thenReturn(title);

        shell.setTitle("");

        verify(builder).setTitle(title);
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.enter");
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.ok");
    }

    @Test
    @SneakyThrows
    void givenEmptyTitleAndEmptyInput_whenSetTitle_thenFail() {
        val builder = mock(Book.Builder.class);
        FieldSetter.setField(shell, shell.getClass().getDeclaredField("bookBuilder"), builder);
        when(io.readLine()).thenReturn("");

        shell.setTitle("");

        verify(builder, never()).setTitle(any());
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.enter");
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.empty");
    }



    @Test
    void whenShowAllBooks_thenSuccess() {
        when(bookDao.getAll()).thenReturn(List.of(book1, book2));

        shell.showAllBooks();

        String out = outputStream.toString(CHARSET);

        bookDao.getAll().forEach( book ->
                assertThat(out)
                        .contains(book.getTitle())
                        .contains(book.getAuthor().getName())
                        .contains(book.getGenres().stream()
                                .map(Genre::getGenre)
                                .collect(toList())));
    }

    @Test
    void whenPrintAuthorVariants_thenSuccess() {
        val author1 = new Author(1, "author1");
        val author2 = new Author(2, "author2");

        shell.printAuthorVariants(List.of(author1, author2));

        assertThat(outputStream.toString(CHARSET))
                .contains("shell.author.variants", author1.getName(), author2.getName());
    }

    @Test
    void givenExistName_whenSetAuthor_thenSuccess() {
        val author1 = new Author(1, "author1");
        when(authorDao.getByName(author1.getName())).thenReturn(Optional.of(author1));

        shell.setAuthor(author1.getName());

        verify(builder).setAuthor(author1);
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.author.ok");
    }

    @Test
    void givenNoArgNameAndExistInputName_whenSetAuthor_thenSuccess() {
        val author1 = new Author(1, "author1");
        when(authorDao.getByName(author1.getName())).thenReturn(Optional.of(author1));
        when(io.readLine()).thenReturn(author1.getName());

        shell.setAuthor("");

        verify(builder).setAuthor(author1);
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.author.enter");
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.author.ok");
    }

    @Test
    void givenNoArgNameAndUnknownInputName_whenSetAuthor_thenSuccess() {
        val author1 = new Author(1, "author1");
        when(authorDao.getByName("unknown")).thenReturn(Optional.empty());
        when(io.readLine()).thenReturn("unknown");

        shell.setAuthor("");

        verify(builder, never()).setAuthor(any());
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.author.enter");
    }

    @Test
    void givenUnknownArgName_whenSetAuthor_thenSuccess() {
        val author1 = new Author(1, "author1");
        when(authorDao.getByName("unknown")).thenReturn(Optional.empty());

        shell.setAuthor("unknown");

        verify(builder, never()).setAuthor(any());
    }

    @Test
    void givenExistGenre_whenAddGenre_thenSuccess() {
        val genre = mock(Genre.class);
        when(genreDao.getByGenre("genre")).thenReturn(Optional.of(genre));

        shell.addGenre("genre");

        verify(builder).addGenre(genre);
    }

    @Test
    void givenUnknownGenre_whenAddGenre_thenNothing() {
        when(genreDao.getByGenre("genre")).thenReturn(Optional.empty());

        shell.addGenre("genre");

        verify(builder, never()).addGenre(any());
    }

    @Test
    void givenExistGenre_whenRemoveGenre_thenSuccess() {
        val genre1 = new Genre(1, "genre1");
        val genre2 = new Genre(2, "genre2");
        when(builder.getGenres()).thenReturn(Set.of(genre1, genre2));

        shell.removeGenre("genre2");

        verify(builder).removeGenre(genre2);
    }

    @Test
    void givenUnknownGenre_whenRemoveGenre_thenNothing() {
        val genre1 = new Genre(1, "genre1");
        val genre2 = new Genre(2, "genre2");
        when(builder.getGenres()).thenReturn(Set.of(genre1, genre2));

        shell.removeGenre("unknown");

        verify(builder, never()).removeGenre(any());
    }

    @Test
    void whenInitBuilder_thenSuccess() {
        val author = mock(Author.class);
        val title = "someTitle";
        val id = 999L;
        val genres = List.of(mock(Genre.class), mock(Genre.class));
        val book = mock(Book.class);
        when(book.getAuthor()).thenReturn(author);
        when(book.getId()).thenReturn(id);
        when(book.getTitle()).thenReturn(title);
        when(book.getGenres()).thenReturn(genres);

        shell.initBuilder(book);

        verify(builder).setGenres(genres);
        verify(builder).setAuthor(author);
        verify(builder).setTitle(title);
        verify(builder).setId(id);
    }

    @Test
    void givenReadyBook_whenDone_thenSuccess() {
        val book = mock(Book.class);
        when(builder.build()).thenReturn(book);
        when(builder.ready()).thenReturn(true);

        shell.done();

        assertThat(outputStream.toString(CHARSET)).contains("shell.book.modified");
        verify(bookDao).save(book);
        verify(state).setState(ShellState.State.ROOT, null);
    }

    @Test
    void givenNotReadyBook_whenDone_thenFail() {
        when(builder.ready()).thenReturn(false);

        shell.done();

        assertThat(outputStream.toString(CHARSET)).contains("shell.book.not-ready");
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.ready-condition");
        verify(bookDao, never()).save(any());
    }

    @Test
    void whenCancel_thenSuccess() {
        shell.cancel();

        verify(state).setState(ShellState.State.ROOT, null);
    }
}