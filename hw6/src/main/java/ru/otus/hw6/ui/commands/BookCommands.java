package ru.otus.hw6.ui.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw6.common.HwException;
import ru.otus.hw6.data.model.Author;
import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;
import ru.otus.hw6.data.model.Genre;
import ru.otus.hw6.services.AuthorService;
import ru.otus.hw6.services.BookService;
import ru.otus.hw6.services.CommentService;
import ru.otus.hw6.services.GenreService;
import ru.otus.hw6.ui.IO;
import ru.otus.hw6.ui.OperationManagement;
import ru.otus.hw6.ui.ShellState;
import ru.otus.hw6.ui.Usage;

import java.util.*;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static ru.otus.hw6.ui.ShellState.State.*;

@ShellComponent
@RequiredArgsConstructor
public class BookCommands implements OperationManagement {
    private final IO io;
    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final CommentService commentService;
    private final ShellState state;

    @Getter
    private Book bookInProcessing;

    private CommentManager commentManager;

    @Override
    public void done() {
        if (isBookReadyToSave(bookInProcessing)) {
            bookService.save(bookInProcessing);
            commentManager.getCommentsToSave().forEach(commentService::save);
            commentManager.getCommentsToRemove().forEach(commentService::delete);
            io.interPrintln("shell.book.modified");
            state.setState(ShellState.State.ROOT, null);
        } else {
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
        ofNullable(bookInProcessing.getTitle()).ifPresentOrElse(
                io::println, () -> io.interPrintln("shell.book.title.not-present"));
        io.interPrint("shell.book.author");
        ofNullable(bookInProcessing.getAuthor()).ifPresentOrElse(
                a -> io.println(a.getName()), () -> io.interPrintln("shell.book.author.not-present"));
        io.interPrint("shell.book.genres").println(
                bookInProcessing.getGenres().stream()
                        .map(Genre::getGenre).collect(joining(", ")));
        io.interPrintln("shell.book.comments");
        commentManager.getComments()
                .forEach(comment -> io.interPrintln(" - " + comment.getComment()));
        commentManager.getCommentsToSave()
                .forEach(comment -> io.interPrintln(" - " + comment.getComment()));

        io.interPrint("shell.book.is-ready");
        if (isBookReadyToSave(bookInProcessing)) {
            io.interPrintln("shell.yes");
        } else {
            io.interPrintln("shell.no");
            io.interPrintln("shell.book.ready-condition");
        }
    }

    @ShellMethod(value = "shell.command.new-book", key = "new-book")
    @ShellMethodAvailability("bookOperationAvailability")
    @Usage("shell.command.new-book.usage")
    public void newBook() {
        bookInProcessing = new Book();
        commentManager = new CommentManager();
        state.setState(NEW_BOOK, this);
    }

    public Availability bookOperationAvailability() {
        return state.getState() == ROOT
                ? Availability.available()
                : Availability.unavailable(io.inter("shell.command.book-root.unavailable-reason"));
    }

    @ShellMethod(value = "shell.command.delete-book", key = "delete-book")
    @ShellMethodAvailability("bookOperationAvailability")
    @Usage("shell.command.delete-book.usage")
    public void deleteBook() {
        try {
            doDeleteBook();
        } catch (HwException e) {
            io.interPrintln(e.getMessage(), e.getParams());
        }
    }

    private void doDeleteBook() {
        var book = getBookByTitleSearch();
        io.interPrint("shell.are-you-sure");
        if (io.readLine().toLowerCase().startsWith("y")) {
            bookService.delete(book);
            io.interPrintln("shell.success");
        }
        else {
            io.interPrintln("shell.cancelled");
        }
    }

    @ShellMethod(value = "shell.command.update-book", key = "update-book")
    @ShellMethodAvailability("bookOperationAvailability")
    @Usage("shell.command.update-book.usage")
    public void updateBook() {
        try {
            doUpdateBook();
        } catch (HwException e) {
            io.interPrintln(e.getMessage(), e.getParams());
        }
    }

    private void doUpdateBook() {
        bookInProcessing = getBookByTitleSearch();
        commentManager = new CommentManager(commentService.getAllByBook(bookInProcessing));
        state.setState(ShellState.State.UPDATE_BOOK, this);
        io.interPrintln("shell.book.updating");
        show();
    }

    @ShellMethod(value = "shell.command.set-title", key = "set-title")
    @ShellMethodAvailability("bookModificationAvailability")
    public void setTitle(@ShellOption(defaultValue = "") String title) {
        if ("".equals(title)) {
            io.interPrint("shell.book.title.enter");
            title = io.readLine();
        }

        if ("".equals(title)) {
            io.interPrintln("shell.book.title.empty");
        } else {
            bookInProcessing.setTitle(title);
            io.interPrintln("shell.book.title.ok", title);
        }
    }

    public Availability bookModificationAvailability() {
        return state.getState() == NEW_BOOK || state.getState() == UPDATE_BOOK
                ? Availability.available() :
                Availability.unavailable(io.inter("shell.command.book-modification.unavailable-reason"));
    }

    @ShellMethod(value = "shell.command.set-author", key = "set-author")
    @ShellMethodAvailability("bookModificationAvailability")
    public void setAuthor() {
        try {
            doSetAuthor();
        } catch (HwException e) {
            io.interPrintln(e.getMessage(), e.getParams());
        }
    }

    private void doSetAuthor() {
        var author = getAuthorByNameSearch();
        bookInProcessing.setAuthor(author);
        io.interPrintln("shell.book.author.ok", author.getName());
    }

    private Author getAuthorByNameSearch() {
        Author author = null;
        while (author == null) {
            io.interPrint("shell.command.search-author.enter-title-pattern");
            var namePattern = io.readLine();
            if ("".equals(namePattern))
                throw new HwException("shell.cancelled");
            var candidates = authorService.searchByNamePart(namePattern);

            if (candidates.isEmpty()) {
                io.interPrintln("shell.command.search-author.no-candidates");
            } else if (candidates.size() > 1) {
                io.interPrintln("shell.command.search-author.multiple-candidates");
                candidates.forEach(a -> io.println(a.getName()));
            } else {
                author = candidates.get(0);
            }
        }
        return author;
    }

    public void printAuthorVariants(List<Author> authors) {
        io.interPrint("shell.author.variants");
        int limit = 7;
        io.print(authors.stream().limit(limit).map(Author::getName).collect(joining(", ")));
        io.println(authors.size() > limit ? " ... (+" + (authors.size() - limit) + ")" : "");
    }

    @ShellMethod(value = "shell.command.add-genre", key = "add-genre")
    @ShellMethodAvailability("bookModificationAvailability")
    public void addGenre(@ShellOption(defaultValue = "") String pattern) {
        try {
            doAddGenre(pattern);
        } catch (HwException e) {
            io.interPrintln(e.getMessage(), e.getParams());
        }
    }

    private void doAddGenre(String pattern) {
        val genre = getGenreByGenrePart(pattern);
        bookInProcessing.getGenres().add(genre);
        io.interPrintln("shell.book.genres.added", genre.getGenre());
    }

    private Genre getGenreByGenrePart(String pattern) {
        Genre genre = null;
        boolean firstAttempt = ! "".equals(pattern);
        while (genre == null) {
            if ( ! firstAttempt)
                pattern = readGenreTitlePattern();
            firstAttempt = false;
            val candidates = genreService.searchByGenrePart(pattern);
            genre = getUniqueGenreOrShowErrorMessage(candidates);
        }
        return genre;
    }

    private String readGenreTitlePattern() {
        io.interPrintln("shell.command.search-genre.enter-title-pattern");
        val pattern = io.readLine();
        if ("".equals(pattern))
            throw new HwException("shell.cancelled");
        return pattern;
    }

    private Genre getUniqueGenreOrShowErrorMessage(List<Genre> genres) {
        if (genres.isEmpty()) {
            io.interPrintln("shell.command.search-genre.no-candidates");
            return null;
        }
        else if (genres.size() > 2) {
            io.interPrintln("shell.command.search-genre.multiple-candidates");
            printGenreVariants(genres);
            return null;
        }
        else {
            return genres.get(0);
        }
    }

    public void printGenreVariants(List<Genre> genres) {
        io.interPrint("shell.genre.variants");
        io.interPrint("shell.genre.variants");
        int limit = 7;
        io.print(genres.stream().limit(limit).map(Genre::getGenre).collect(joining(", ")));
        io.println(genres.size() > limit ? " ... (+ " + (genres.size() - limit) + ")" : "");
    }

    @ShellMethod(value = "shell.command.remove-genre", key = "remove-genre")
    @ShellMethodAvailability("bookModificationAvailability")
    public void removeGenre(@ShellOption(defaultValue = "") String pattern) {
        try {
            doRemoveGenre(pattern);
        } catch (HwException e) {
            io.interPrintln(e.getMessage(), e.getParams());
        }
    }

    private void doRemoveGenre(String pattern) {
        val genre = getGenreByGenrePart(pattern);
        bookInProcessing.getGenres().remove(genre);
        io.interPrintln("shell.genre.removed", genre.getGenre());
        showBookGenres(bookInProcessing.getGenres());
    }

    @ShellMethod(value = "shell.command.add-comment", key = "add-comment")
    @ShellMethodAvailability("bookModificationAvailability")
    public void addComment() {
        io.interPrintln("shell.command.add-comment.description");
        val text = io.readMultilineString();
        if (!"".equals(text)) {
            val comment = new Comment(0, bookInProcessing, text);
            commentManager.addNewComment(comment);
            io.interPrintln("shell.success");
        } else
            io.interPrintln("shell.command.add-comment.should-not-be-empty");
    }

    @ShellMethod(value = "shell.command.remove-comment", key = "remove-comment")
    @ShellMethodAvailability("bookModificationAvailability")
    public void removeComment() {
        try {
            doRemoveComment();
        } catch (HwException e) {
            io.interPrintln(e.getMessage(), e.getParams());
        }
    }

    private void doRemoveComment() {
        val comments = showBookCommentsAndGetAsList();
        io.interPrint("shell.command.remove-comment.description");
        int num = io.readIntInBounds(0, comments.size() - 1);
        val comment = comments.get(num);
        commentManager.markToRemove(comment);
        io.interPrintln("shell.success");
    }

    @ShellMethod(value = "shell.command.update-comment", key = "update-comment")
    @ShellMethodAvailability("bookModificationAvailability")
    public void updateComment() {
        try {
            doUpdateComment();
        } catch (HwException e) {
            io.interPrintln(e.getMessage(), e.getParams());
        }
    }

    private void doUpdateComment() {
        io.interPrintln("shell.command.update-comment.description");
        val comments = showBookCommentsAndGetAsList();
        int num = io.readIntInBounds(0, comments.size() - 1);
        val comment = comments.get(num);

        io.interPrintln("shell.command.update-comment.enter-text");
        val text = io.readMultilineString();
        if (!"".equals(text)) {
            comment.setComment(text);
            commentManager.updateComment(comment);
            io.interPrintln("shell.success");
        } else
            io.interPrintln("shell.command.update-comment.should-not-be-empty");
    }

    private List<Comment> showBookCommentsAndGetAsList() {
        io.interPrintln("shell.book.comments");
        val comments = new ArrayList<Comment>();
        comments.addAll(commentManager.getComments());
        comments.addAll(commentManager.getCommentsToSave());
        comments.stream()
                .map(c -> comments.indexOf(c) + ") " + c.getComment())
                .forEach(io::println);
        return comments;
    }

    @ShellMethod(value = "shell.command.all-books", key = "all-books")
    @Usage("shell.command.all-books.usage")
    public void showAllBooks() {
        bookService.getAll().forEach(this::showBookWithoutComments);
    }

    public void showBookGenres(Collection<Genre> genres) {
        io.interPrint("shell.genre.current");
        io.println(genres.stream().map(Genre::getGenre).collect(joining(", ")));
    }

    private void showSeparator() {
        io.println("===========================================");
    }

    @ShellMethod(value = "shell.command.show-book", key = "show-book")
    @Usage("shell.command.show-book.usage")
    public void showSingleBook() {
        try {
            doShowSingleBook();
        }
        catch (HwException e) {
            io.interPrintln(e.getMessage(), e.getParams());
        }
    }

    private void doShowSingleBook() {
        val book = getBookByTitleSearch();
        showBookWithoutComments(book);
        showBookComments(book);
    }

    private Book getBookByTitleSearch() {
        Book book = null;
        while (book == null) {
            io.interPrint("shell.command.search-book.enter-title-pattern");
            var titlePattern = io.readLine();
            if ("".equals(titlePattern))
                throw new HwException("shell.cancelled");
            var candidates = bookService.searchByTitlePart(titlePattern);

            if (candidates.isEmpty()) {
                io.interPrintln("shell.command.search-book.no-candidates");
            } else if (candidates.size() > 1) {
                io.interPrintln("shell.command.search-book.multiple-candidates");
                candidates.forEach(this::showBookWithoutComments);
            } else {
                book = candidates.get(0);
            }
        }
        return book;
    }

    private void showBookWithoutComments(Book book) {
        showSeparator();
        io.interPrint("shell.book.title").println(book.getTitle())
                .interPrint("shell.book.author").println(book.getAuthor().getName())
                .interPrint("shell.book.genres")
                .println(book.getGenres().stream()
                        .map(Genre::getGenre)
                        .collect(joining(", ")));
    }

    private void showBookComments(Book book) {
        io.interPrintln("shell.book.comments")
                .println(commentService.getAllByBook(book).stream()
                        .map(c -> " - " + c.getComment())
                        .collect(joining("\n")));
    }

    private boolean isBookReadyToSave(Book book) {
        return book.getTitle() != null
                && !"".equals(book.getTitle())
                && book.getAuthor() != null;
    }
}
