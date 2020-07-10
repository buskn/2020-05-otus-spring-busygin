package ru.otus.hw5.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw5.dao.Author;
import ru.otus.hw5.dao.AuthorDao;
import ru.otus.hw5.dao.Genre;

import static java.util.Comparator.comparing;

@ShellComponent
@RequiredArgsConstructor
public class AuthorCommands {
    private final AuthorDao authorDao;
    private final IO io;
    private final ShellState state;

    @ShellMethod(value = "shell.command.all-authors", key = "all-authors")
    @ShellMethodAvailability("onlyFromRootAvailable")
    public void allAuthors() {
        io.interPrintln("shell.genre.all");
        authorDao.getAll().stream().sorted(comparing(Author::getName)).forEach(author -> {
            io.printf("%s (%s)\n", author.getName(), author.getId());
        });
    }

    public Availability onlyFromRootAvailable() {
        return state.getState() == ShellState.State.ROOT
                ? Availability.available()
                : Availability.unavailable(
                io.inter("shell.command.all-genres.unavailable-reason"));
    }
}
