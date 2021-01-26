package ru.otus.hw6.ui;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.model.Author;
import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;
import ru.otus.hw6.data.model.Genre;
import ru.otus.hw6.services.AuthorService;
import ru.otus.hw6.services.BookService;
import ru.otus.hw6.services.CommentService;
import ru.otus.hw6.services.GenreService;
import ru.otus.hw6.ui.commands.BookCommands;
import ru.otus.hw6.ui.commands.CommentManager;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookCommandsTest {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Configuration
    @Import(BookCommands.class)
    static class Config {
    }

    @Autowired
    private BookCommands shell;

    @MockBean
    IO io;
    @MockBean
    BookService bookService;
    @MockBean
    AuthorService authorService;
    @MockBean
    GenreService genreService;
    @MockBean
    CommentService commentService;
    @MockBean
    ShellState state;

    CommentManager commentManager;

    private TestData data;

    private ByteArrayOutputStream outputStream;

    private Book bookInProcessing;

    @BeforeEach
    @SneakyThrows
    private void setUp() {
        data = new TestData();

        commentManager = new CommentManager();

        FieldSetter.setField(shell, shell.getClass().getDeclaredField("commentManager"), commentManager);

        bookInProcessing = spy(data.BOOK_1);
        FieldSetter.setField(shell, shell.getClass().getDeclaredField("bookInProcessing"), bookInProcessing);

        outputStream = new ByteArrayOutputStream();
        when(io.print(any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.println(any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.interPrint(any(), any())).then(inv -> pushArgsToOut(inv.getArguments()));
        when(io.interPrintln(any(), any())).then(inv -> pushArgsToOut(inv.getArguments()));
    }

    @SneakyThrows
    private IO pushArgsToOut(Object[] args) {
        for (Object arg : args)
            outputStream.write((arg + "|").getBytes(CHARSET));
        return io;
    }

    @Test
    void givenExistMultiSearchAndSure_whenDeleteBook_thenSuccess() {
        val multiPattern = "book";
        val multiList = data.BOOKS;
        val book = data.BOOK_FOR_DELETE;
        val uniquePattern = book.getTitle().toLowerCase();
        when(io.readLine())
                .thenReturn(multiPattern)
                .thenReturn(multiPattern)
                .thenReturn(uniquePattern)
                .thenReturn("yeah");
        when(bookService.searchByTitlePart(multiPattern))
                .thenReturn(multiList);
        when(bookService.searchByTitlePart(uniquePattern))
                .thenReturn(List.of(book));

        shell.deleteBook();

        verify(bookService).delete(book);

        val out = outputStream.toString(CHARSET);
        multiList.forEach(b -> {
            assertThat(out)
                    .contains(b.getTitle())
                    .contains(b.getAuthor().getName());
            b.getGenres().forEach(g -> assertThat(out).contains(g.getGenre()));
        });
        assertThat(outputStream.toString(CHARSET)).contains("shell.success");
    }

    @Test
    void givenExistMultiSearchAndNotSure_whenDeleteBook_thenSuccess() {
        val multiPattern = "book";
        val multiList = data.BOOKS;
        val book = data.BOOK_FOR_DELETE;
        val uniquePattern = book.getTitle().toLowerCase();
        when(io.readLine())
                .thenReturn(multiPattern)
                .thenReturn(multiPattern)
                .thenReturn(uniquePattern)
                .thenReturn("nope!");
        when(bookService.searchByTitlePart(multiPattern))
                .thenReturn(multiList);
        when(bookService.searchByTitlePart(uniquePattern))
                .thenReturn(List.of(book));

        shell.deleteBook();

        verify(bookService, never()).delete(any());

        val out = outputStream.toString(CHARSET);
        multiList.forEach(b -> {
            assertThat(out)
                    .contains(b.getTitle())
                    .contains(b.getAuthor().getName());
            b.getGenres().forEach(g -> assertThat(out).contains(g.getGenre()));
        });
        assertThat(outputStream.toString(CHARSET)).contains("shell.cancelled");
    }

    @Test
    void givenEmptyLineInput_whenDeleteBook_thenCancel() {
        when(io.readLine()).thenReturn("");

        shell.deleteBook();

        verify(bookService, never()).delete(any());
        assertThat(outputStream.toString(CHARSET)).contains("shell.cancelled");
    }

    @Test
    @SneakyThrows
    void whenShowBookState_thenSuccess() {
        var book = bookInProcessing;

        shell.show();

        val out = outputStream.toString(CHARSET);
        assertThat(out).contains(book.getTitle(), book.getAuthor().getName());
        book.getGenres().forEach(genre -> assertThat(out).contains(genre.getGenre()));
    }

    @Test
    @SneakyThrows
    void givenTitle_whenSetTitle_thenSuccess() {
        val title = "someTitle";

        shell.setTitle(title);

        verify(bookInProcessing).setTitle(title);
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.ok");
    }

    @Test
    @SneakyThrows
    void givenEmptyTitle_whenSetTitle_thenSuccess() {
        val title = "someTitle";
        when(io.readLine()).thenReturn(title);

        shell.setTitle("");

        assertThat(bookInProcessing.getTitle()).isEqualTo(title);
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.enter");
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.ok");
    }

    @Test
    void givenEmptyTitleAndEmptyInput_whenSetTitle_thenFail() {
        when(io.readLine()).thenReturn("");

        shell.setTitle("");

        verify(bookInProcessing, never()).setTitle(any());
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.enter");
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.title.empty");
    }


    @Test
    void whenShowAllBooks_thenSuccess() {
        when(bookService.getAll()).thenReturn(data.BOOKS);

        shell.showAllBooks();

        String out = outputStream.toString(CHARSET);

        bookService.getAll().forEach(book -> {
            assertThat(out)
                    .contains(book.getTitle())
                    .contains(book.getAuthor().getName());
            if (!book.getGenres().isEmpty())
                assertThat(out).contains(book.getGenres().stream()
                        .map(Genre::getGenre)
                        .collect(toList()));
        });
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
    void givenExistSingleName_whenSetAuthor_thenSuccess() {
        val author1 = new Author(1, "author1");
        when(authorService.searchByNamePart(author1.getName()))
                .thenReturn(List.of(author1));
        when(io.readLine()).thenReturn(author1.getName());

        shell.setAuthor();

        verify(bookInProcessing).setAuthor(author1);
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.author.ok");
    }

    @Test
    void givenEmptyLine_whenSetAuthor_thenSuccess() {
        when(io.readLine()).thenReturn("");

        shell.setAuthor();

        verify(bookInProcessing, never()).setAuthor(any());
        assertThat(outputStream.toString(CHARSET)).contains("shell.cancelled");
    }

    @Test
    void givenExistMultiName_whenSetAuthor_thenSuccess() {
        val multiPattern = "author\n";
        val author = data.AUTHOR_2;
        val uniquePattern = author.getName().toLowerCase();
        when(io.readLine())
                .thenReturn(multiPattern)
                .thenReturn(multiPattern)
                .thenReturn(uniquePattern);
        when(authorService.searchByNamePart(multiPattern))
                .thenReturn(data.AUTHORS);
        when(authorService.searchByNamePart(uniquePattern))
                .thenReturn(List.of(author));

        shell.setAuthor();

        verify(bookInProcessing).setAuthor(author);
        val out = outputStream.toString(CHARSET);
        data.AUTHORS.forEach(a -> assertThat(out).contains(a.getName()));
    }

    @Test
    void givenExistMultiPattern_whenAddGenre_thenSuccess() {
        val genre = data.GENRE_FOR_DELETE;
        val multiPattern = "genre";
        val uniquePattern = genre.getGenre().toUpperCase();
        when(io.readLine())
                .thenReturn(multiPattern)
                .thenReturn(multiPattern)
                .thenReturn(uniquePattern);
        when(genreService.searchByGenrePart(multiPattern))
                .thenReturn(data.GENRES);
        when(genreService.searchByGenrePart(uniquePattern))
                .thenReturn(List.of(genre));

        shell.addGenre(multiPattern);

        assertThat(bookInProcessing.getGenres()).contains(genre);
    }

    @Test
    void givenExistUniqueArgPattern_whenAddGenre_thenSuccess() {
        val genre = data.GENRE_FOR_DELETE;
        val uniquePattern = genre.getGenre().toUpperCase();
        when(genreService.searchByGenrePart(uniquePattern))
                .thenReturn(List.of(genre));

        shell.addGenre(uniquePattern);

        assertThat(bookInProcessing.getGenres()).contains(genre);
    }

    @Test
    void givenEmptyLineInput_whenAddGenre_thenCancel() {
        val initGenres = new ArrayList<>(bookInProcessing.getGenres());
        when(io.readLine())
                .thenReturn("");

        shell.addGenre("some");

        assertThat(bookInProcessing.getGenres())
                .containsExactlyInAnyOrderElementsOf(initGenres);
        assertThat(outputStream.toString(CHARSET))
                .contains("shell.cancelled");
    }

    @Test
    void givenExistMultiPattern_whenRemoveGenre_thenSuccess() {
        val genre = data.GENRE_FOR_DELETE;
        val multiPattern = "genre";
        val uniquePattern = genre.getGenre().toUpperCase();
        when(io.readLine())
                .thenReturn(multiPattern)
                .thenReturn(multiPattern)
                .thenReturn(uniquePattern);
        when(genreService.searchByGenrePart(multiPattern))
                .thenReturn(data.GENRES);
        when(genreService.searchByGenrePart(uniquePattern))
                .thenReturn(List.of(genre));

        bookInProcessing.getGenres().add(genre);
        val controlSample = new ArrayList<>(bookInProcessing.getGenres());
        controlSample.remove(genre);

        shell.removeGenre(multiPattern);

        assertThat(bookInProcessing.getGenres())
                .containsExactlyInAnyOrderElementsOf(controlSample);
    }

    @Test
    void givenExistUniqueArgPattern_whenRemoveGenre_thenSuccess() {
        val genre = data.GENRE_FOR_DELETE;
        bookInProcessing.getGenres().add(genre);

        val uniquePattern = genre.getGenre().toUpperCase();
        when(genreService.searchByGenrePart(uniquePattern))
                .thenReturn(List.of(genre));

        val initGenres = new ArrayList<>(bookInProcessing.getGenres());
        assertThat(initGenres).contains(genre);

        shell.removeGenre(uniquePattern);

        initGenres.remove(genre);
        assertThat(bookInProcessing.getGenres())
                .containsExactlyInAnyOrderElementsOf(initGenres);
    }

    @Test
    void givenEmptyLineInput_whenRemoveGenre_thenCancel() {
        val initGenres = new ArrayList<>(bookInProcessing.getGenres());

        when(io.readLine())
                .thenReturn("");

        shell.removeGenre("some");

        assertThat(bookInProcessing.getGenres())
                .containsExactlyInAnyOrderElementsOf(initGenres);
        assertThat(outputStream.toString(CHARSET))
                .contains("shell.cancelled");
    }

    // TODO how to test comments work now?
    @Test
    void givenMultiline_whenAddComment_thenSuccess() {
        val text = "asd\nzxc\nert";
        val book = data.BOOK_1;
        val bookPattern = book.getTitle();
        when(io.readLine()).thenReturn(bookPattern);
        when(io.readMultilineString()).thenReturn(text);
        when(bookService.searchByTitlePart(bookPattern)).thenReturn(List.of(book));

        shell.updateBook();
        shell.addComment();
        shell.done();

        val captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentService).save(captor.capture());
        assertThat(captor.getValue().getComment()).isEqualTo(text);
    }

    @Test
    void givenEmptyLine_whenAddComment_thenCancel() {
        val text = "";
        val book = data.BOOK_1;
        val bookPattern = book.getTitle();
        when(io.readLine()).thenReturn(bookPattern);
        when(io.readMultilineString()).thenReturn(text);
        when(bookService.searchByTitlePart(bookPattern)).thenReturn(List.of(book));

        shell.updateBook();
        shell.addComment();
        shell.done();

        verify(commentService, never()).save(any());
    }

    @Test
    void givenRightCommentNumber_whenRemoveComment_thenSuccess() {
        val text = "0";
        val book = data.BOOK_1;
        val bookPattern = book.getTitle();
        val commentToDelete = data.COMMENT_FOR_DELETE;
        val commentToPersist = data.COMMENT_3;
        when(io.readLine())
                .thenReturn(bookPattern)
                .thenReturn(text);
        when(bookService.searchByTitlePart(bookPattern))
                .thenReturn(List.of(book));
        when(commentService.getAllByBook(book))
                .thenReturn(List.of(commentToDelete, commentToPersist));

        shell.updateBook();
        shell.removeComment();
        shell.done();

        verify(commentService).delete(commentToDelete);
    }

    @Test
    void givenRightCommentNumber_whenUpdateComment_thenSuccess() {
        val text = "0";
        val book = data.BOOK_2;
        val bookPattern = book.getTitle();
        val commentToUpdate = data.COMMENT_4;
        when(io.readLine())
                .thenReturn(bookPattern)
                .thenReturn(text);
        when(io.readMultilineString())
                .thenReturn(data.COMMENT_4_UPDATED.getComment());
        when(bookService.searchByTitlePart(bookPattern))
                .thenReturn(List.of(book));
        when(commentService.getAllByBook(book))
                .thenReturn(List.of(commentToUpdate));

        shell.updateBook();
        shell.updateComment();
        shell.done();

        verify(commentService).save(commentToUpdate);
    }

    @Test
    void givenReadyBook_whenDone_thenSuccess() {
        commentManager.addNewComment(data.COMMENT_1);

        shell.done();

        assertThat(outputStream.toString(CHARSET)).contains("shell.book.modified");
        verify(bookService).save(bookInProcessing);
        verify(state).setState(ShellState.State.ROOT, null);
        verify(commentService).save(data.COMMENT_1);
    }

    @Test
    void givenNotReadyBook_whenDone_thenFail() {
        bookInProcessing.setAuthor(null);

        shell.done();

        assertThat(outputStream.toString(CHARSET)).contains("shell.book.not-ready");
        assertThat(outputStream.toString(CHARSET)).contains("shell.book.ready-condition");
        verify(bookService, never()).save(any());
    }

    @Test
    void whenCancel_thenSuccess() {
        shell.cancel();

        verify(state).setState(ShellState.State.ROOT, null);
    }
}