package ru.otus.hw5.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw5.dao.Genre;
import ru.otus.hw5.dao.GenreDao;

import static java.util.Comparator.*;

@ShellComponent
@RequiredArgsConstructor
public class GenreCommands {
    private final GenreDao genreDao;
    private final IO io;
    private final ShellState state;

    @ShellMethod(value = "shell.command.all-genres", key = "all-genres")
    @ShellMethodAvailability("onlyFromRootAvailable")
    public void allGenres() {
        io.interPrintln("shell.genre.all");
        genreDao.getAll().stream().sorted(comparing(Genre::getGenre)).forEach(genre -> {
            io.printf("%s (%s)\n", genre.getGenre(), genre.getId());
        });
    }

    public Availability onlyFromRootAvailable() {
        return state.getState() == ShellState.State.ROOT
                ? Availability.available()
                : Availability.unavailable(
                        io.inter("shell.command.all-genres.unavailable-reason"));
    }
}
