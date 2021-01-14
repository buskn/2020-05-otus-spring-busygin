package ru.otus.hw6.ui.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw6.dao.Author;
import ru.otus.hw6.dao.AuthorDao;
import ru.otus.hw6.ui.IO;
import ru.otus.hw6.ui.ShellState;
import ru.otus.hw6.ui.Usage;

import static java.util.Comparator.comparing;

@ShellComponent
@RequiredArgsConstructor
public class AuthorCommands {
    private final AuthorDao authorDao;
    private final IO io;
    private final ShellState state;

    @ShellMethod(value = "shell.command.all-authors", key = "all-authors")
    @ShellMethodAvailability("onlyFromRootAvailable")
    @Usage("shell.command.all-authors.usage")
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
                io.inter("shell.command.all-authors.unavailable-reason"));
    }
}
