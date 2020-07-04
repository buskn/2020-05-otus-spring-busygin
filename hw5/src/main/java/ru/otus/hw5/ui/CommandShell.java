package ru.otus.hw5.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Shell;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw5.dao.BookDao;
import ru.otus.hw5.dao.Genre;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class CommandShell {
    private final IO io;
    private final BookDao bookDao;

    @ShellMethod(value = "вывести информацию обо всех книгах", key = "all-books")
    public void showAllBooks() {
        io.println("Информация обо всех книгах");
        bookDao.getAll().forEach( book -> {
            showSeparator();
            io.println("Book title: " + book.getTitle());
            io.println("Author: " + book.getAuthor().getName());
            io.println("Genres: " +
                    book.getGenres().stream().map(Genre::getGenre).collect(Collectors.joining(", ")));
        });
    }

    private void showSeparator() {
        io.println("===========================================");
    }
}
