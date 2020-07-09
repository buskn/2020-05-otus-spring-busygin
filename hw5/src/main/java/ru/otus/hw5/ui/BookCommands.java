package ru.otus.hw5.ui;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw5.config.Settings;
import ru.otus.hw5.dao.*;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static ru.otus.hw5.ui.ShellState.State.NEW_BOOK;
import static ru.otus.hw5.ui.ShellState.State.ROOT;

@ShellComponent
@RequiredArgsConstructor
public class BookCommands implements OperationCommands {
    private final IO io;
    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final ShellState state;

    private Book.Builder bookBuilder;

    @Override
    public void done() {
        if (bookBuilder.ready()) {
            bookDao.save(bookBuilder.build());
            io.interPrintln("shell.book.modified");
            state.setState(ShellState.State.ROOT, null);
        }
        else {
            io.interPrintln("shell.book.not-ready");
            io.interPrintln("shell.book.ready-condition");
        }
    }

    @Override
    public void cancel() {
        state.setState(ShellState.State.ROOT, null);
    }

    @Override
    public void show() {
        io.interPrint("shell.book.title");
        bookBuilder.getTitle().ifPresentOrElse(
                io::println, () -> io.interPrintln("shell.book.title.not-present"));
        io.interPrint("shell.book.author");
        bookBuilder.getAuthor().ifPresentOrElse(
                a -> io.println(a.getName()), () -> io.interPrintln("shell.book.author.not-present"));
        io.interPrint("shell.book.genres").println(
                bookBuilder.getGenres().stream()
                        .map(Genre::getGenre).collect(joining(", ")));
        io.interPrint("shell.book.is-ready");
        io.interPrintln(bookBuilder.ready() ? "shell.yes" : "shell.no");
        if ( ! bookBuilder.ready() ) {
            io.interPrintln("shell.book.ready-condition");
        }
    }

    @ShellMethod(value = "shell.command.new-book", key = "new-book")
    @ShellMethodAvailability("bookOperationAvailability")
    public void newBook() {
        bookBuilder = new Book.Builder();
        state.setState(NEW_BOOK, this);
    }

    public Availability bookOperationAvailability() {
        return state.getState() == ROOT
                ? Availability.available() : Availability.unavailable("you must be in root level");
    }

    @ShellMethod(value = "shell.command.delete-book", key = "delete-book")
    @ShellMethodAvailability("bookOperationAvailability")
    public void deleteBook(@ShellOption long id) {
        bookDao.getById(id).ifPresentOrElse(
                book -> {
                    bookDao.delete(id);
                    io.interPrintln("shell.book.deleted");
                },
                () -> io.interPrintln("shell.book.not-found", id)
        );
    }

    @ShellMethod(value = "shell.command.update-book", key = "update-book")
    @ShellMethodAvailability("bookOperationAvailability")
    public void updateBook(@ShellOption long id) {
        bookDao.getById(id).ifPresentOrElse(
            book -> {
                bookBuilder = new Book.Builder();
                initBuilder(book);
                state.setState(ShellState.State.UPDATE_BOOK, this);
                io.interPrintln("shell.book.updating");
                show();
            },
            () -> io.interPrintln("shell.book.not-found", id)
        );
    }

    public void initBuilder(Book book) {
        bookBuilder.setId(book.getId());
        bookBuilder.setTitle(book.getTitle());
        bookBuilder.setAuthor(book.getAuthor());
        bookBuilder.setGenres(book.getGenres());
    }

    @ShellMethod(value = "shell.command.set-title", key = "set-title")
    @ShellMethodAvailability("bookConstructionAvailability")
    public void setTitle(@ShellOption(defaultValue = "") String title) {
        if ("".equals(title)) {
            io.interPrint("shell.book.title.enter");
            title = io.readLine();
        }

        if ("".equals(title)) {
            io.interPrintln("shell.book.title.empty");
        }
        else {
            bookBuilder.setTitle(title);
            io.interPrintln("shell.book.title.ok", title);
        }
    }

    public Availability bookConstructionAvailability() {
        return state.getState() == NEW_BOOK
                ? Availability.available() : Availability.unavailable("you not started new-book operation");
    }

    @ShellMethod(value = "shell.command.set-author", key = "set-author")
    @ShellMethodAvailability("bookConstructionAvailability")
    public void setAuthor(@ShellOption(defaultValue = "") String name) {
        if ("".equals(name)) {
            io.interPrint("shell.book.author.enter");
            name = io.readLine();
        }
        val name0 = name;
        authorDao.getByName(name).ifPresentOrElse(
                author -> {
                    bookBuilder.setAuthor(author);
                    io.interPrintln("shell.book.author.ok", author.getName());
                },
                () -> printAuthorVariants(authorDao.searchByNamePart(name0))
        );
    }

    public void printAuthorVariants(List<Author> authors) {
        io.interPrint("shell.author.variants");
        int limit = 7;
        io.print(authors.stream().limit(limit).map(Author::getName).collect(joining(", ")));
        io.println( authors.size() > limit ? " ... (+" + (authors.size() - limit) + ")" : "" );
    }

    @ShellMethod(value = "shell.command.add-genre", key = "add-genre")
    @ShellMethodAvailability("bookConstructionAvailability")
    public void addGenre(@ShellOption(defaultValue = "") String genre) {
        if ("".equals(genre)) {
            io.interPrint("shell.book.genres.enter");
            genre = io.readLine();
        }
        val genre0 = genre;
        genreDao.getByGenre(genre).ifPresentOrElse(
                g -> {
                    bookBuilder.addGenre(g);
                    io.interPrintln("shell.book.genres.added", g.getGenre());
                },
                () -> printGenreVariants(genreDao.searchByGenrePart(genre0))
        );
    }

    public void printGenreVariants(List<Genre> genres) {
        io.interPrint("shell.genre.variants");
        int limit = 7;
        io.print(genres.stream().limit(limit).map(Genre::getGenre).collect(joining(", ")));
        io.println( genres.size() > limit ? " ... (+ " + (genres.size() - limit) + "" : "" );
    }

    @ShellMethod(value = "shell.command.remove-genre", key = "remove-genre")
    public void removeGenre(@ShellOption(defaultValue = "") String genre) {
        if ("".equals(genre)) {
            io.interPrint("shell.genre.enter");
            genre = io.readLine();
        }
        val genre0 = genre.trim().toLowerCase();
        bookBuilder.getGenres().stream()
                .filter(g -> g.getGenre().trim().toLowerCase().equals(genre0))
                .findFirst()
                .ifPresentOrElse(
                        g -> {
                            bookBuilder.removeGenre(g);
                            io.interPrintln("shell.genre.removed", g.getGenre());
                        },
                        () -> io.interPrintln("shell.genre.not-found", genre0)
                );
        showBookGenres(bookBuilder.getGenres());
    }

    @ShellMethod(value = "shell.command.all-books", key = "all-books")
    public void showAllBooks() {
        bookDao.getAll().forEach( book -> {
            showSeparator();
            io.interPrintln("shell.book.id") .println(book.getId())
                .interPrint("shell.book.title") .println(book.getTitle())
                .interPrint("shell.book.author") .println(book.getAuthor().getName())
                .interPrint("shell.book.genres")
                .println(book.getGenres().stream()
                        .map(Genre::getGenre)
                        .collect(joining(", ")));
        });
    }

    public void showBookGenres(Collection<Genre> genres) {
        io.interPrint("shell.genre.current");
        io.println(genres.stream().map(Genre::getGenre).collect(joining(", ")));
    }

    private void showSeparator() {
        io.println("===========================================");
    }
}
